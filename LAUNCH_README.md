# 邀请码模块上线注意事项

> 适用范围：`sys_invite_code` 邀请码生成与分佣结算链路
> 关键词：Redis 发号器、seq ↔ 邀请码 一一映射、永久不变、可解码溯源

---

## 一、机制简介

| 组件 | 作用 | 文件 |
|------|------|------|
| `RedisInviteSeqGenerator` | Redis `INCR` 原子自增生成全局唯一 `seq`，启动时从 DB 兜底同步 | `service/permission/invite/impl/RedisInviteSeqGenerator.java` |
| `InviteCodeUtil` | `seq` ↔ 8 位邀请码 一一对应的 `encode/decode`（54 进制） | `utils/InviteCodeUtil.java` |
| `InviteCodeServiceImpl` | 业务编排：1人1码校验 → 取 seq → 编码 → 入库 | `service/permission/invite/impl/InviteCodeServiceImpl.java` |

**容量上限**：8 位 54 进制 = `54^8 - 1 = 72,301,961,339,135`（约 7.23 万亿），按业务规模永远用不完。

**核心特性**：
- 数学上**无碰撞**，`seq ∈ [0, 54^8-1]` 与 8 位邀请码严格一一对应
- **可逆溯源**：`InviteCodeUtil.decode(inviteCode)` 反查 `seq`，对账友好
- 邀请码**永久不变**，适合长期分佣结算

---

## 二、Redis 持久化要求（强烈建议）

Redis 是 `seq` 发号器的唯一来源，**Redis 丢号 = 重新发号会碰撞**。生产环境必须开启持久化：

### 推荐配置：AOF + RDB 双保险

```conf
# redis.conf
appendonly yes                  # 开启 AOF
appendfsync everysec             # 每秒刷盘，宕机最多丢 1s 数据
auto-aof-rewrite-percentage 100 # AOF 文件比上次重写大 100% 时自动重写
auto-aof-rewrite-min-size 64mb

save 900 1                       # RDB 兜底：900s 内 1 次修改触发快照
save 300 10
save 60 10000
```

### 容灾顺序
1. Redis 进程崩溃 → AOF 重启恢复（最多丢 1s）
2. AOF 损坏 → RDB 快照恢复
3. AOF + RDB 都丢 → **应用启动时 `@PostConstruct` 自动从 DB `MAX(seq)` 兜底同步**（见下一节）

---

## 三、启动同步兜底（已实现，无需运维介入）

### 自动行为

`RedisInviteSeqGenerator#syncSeqFromDbOnStartup()` 通过 `@PostConstruct` 在应用启动时自动执行：

1. 查询 `SELECT MAX(seq) FROM sys_invite_code`（含逻辑删除行，因 seq 一旦消费永不复用）
2. 用 Lua 脚本**原子化 compare-and-set**：
   - 若 Redis 当前值 `< DB 最大值` → 覆盖为 DB 最大值，下次 INCR 返回 `DB_MAX + 1`
   - 若 Redis 已领先或相等 → 不动（避免回退）
3. 同步失败（Redis 不可用）**不阻塞应用启动**，首次发号时 Redis 异常会自然暴露

### 日志关键字（上线后请验证）

启动日志中应能看到以下之一：
- `Redis seq 启动同步：sys_invite_code 表为空，跳过（首次发号将从 seq=1 开始）` —— 全新部署
- `Redis seq 启动同步：DB 最大 seq=xxx, Redis 已同步到该值` —— Redis 丢号后已恢复
- `Redis seq 启动同步：DB 最大 seq=xxx, Redis 已领先或相等，无需同步` —— 正常情况
- `Redis seq 启动同步失败（Redis 不可用？）...` —— Redis 未就绪，需排查

### 手动触发（紧急恢复场景）

如需在不停机情况下重新同步（例如 Redis 主备切换后），可调用：

```java
// 通过管理端 / actuator 暴露后调用
redisInviteSeqGenerator.syncSeqFromDb();
```

---

## 四、上线操作步骤（dev → prod）

### Step 1：数据库迁移

**全新部署**：直接执行 `src/main/resources/sql/rbac.sql`，`sys_invite_code` 表已包含 `seq` 列。

**存量库迁移**（dev 库已有数据但无 seq 列）：

```sql
-- 1. 加列（先用 DEFAULT 0 绕开 NOT NULL 约束，待回填后改为 NOT NULL）
ALTER TABLE sys_invite_code ADD COLUMN seq BIGINT NOT NULL DEFAULT 0 COMMENT '原始序列号' AFTER id;

-- 2. 回填存量行的 seq：对每个 invite_code 调用 InviteCodeUtil.decode() 反查 seq
--    Java 侧执行一次脚本（伪代码）：
--    SELECT id, invite_code FROM sys_invite_code;
--    for each row: seq = InviteCodeUtil.decode(row.inviteCode);
--                  UPDATE sys_invite_code SET seq = ? WHERE id = ?;

-- 3. 加唯一索引（必须先回填完毕，否则重复 seq 会失败）
ALTER TABLE sys_invite_code ADD UNIQUE KEY uk_seq (seq);
```

> ⚠️ 存量邀请码若由旧方案（雪花 ID 取模 / 纯随机）生成，**`decode()` 会抛异常**——那些码不可逆。
> 处理策略（任选其一）：
> - **清空 dev 数据**：`TRUNC TABLE sys_invite_code;`（开发期推荐，最干净）
> - **保留旧码、跳过 seq 唯一约束**：把旧码 seq 全部回填为 0，放弃溯源能力
> - **重新生成**：删除旧码行，让用户重新触发"生成邀请码"

### Step 2：Redis 配置检查

```bash
# 登录 prod Redis
redis-cli
> CONFIG GET appendonly           # 应为 yes
> CONFIG GET save                 # 应非空
> INFO persistence                # 检查 aof_enabled=1 / rdb_last_bgsave_status=ok
```

### Step 3：应用配置

`application-prod.yml`（或对应 profile）确认 Redis 连接配置：

```yaml
spring:
  data:
    redis:
      host: <prod-redis-host>
      port: 6379
      password: <prod-redis-password>
      timeout: 3000ms
      lettuce:
        pool:
          max-active: 16
          max-idle: 8
          min-idle: 2
```

### Step 4：启动验证

应用启动后立即检查日志，确认看到"Redis seq 启动同步"信息。

如果首次部署（DB 表为空），日志应为：
```
Redis seq 启动同步：sys_invite_code 表为空，跳过（首次发号将从 seq=1 开始）
```

### Step 5：功能冒烟

```
1. 用一个测试账号调用"生成邀请码"接口
2. 检查 sys_invite_code 表新行：seq、invite_code 字段均已填充
3. 调用 InviteCodeUtil.decode(inviteCode) 验证能反解回相同 seq
4. 再次用同一账号调用"生成邀请码"，应返回"邀请码已存在"，不重复发号
```

---

## 五、运维检查清单

| 检查项 | 命令/位置 | 期望状态 | 频率 |
|--------|----------|---------|------|
| Redis AOF 开启 | `CONFIG GET appendonly` | `yes` | 上线时 + 每周巡检 |
| Redis 最近一次 BGSAVE 状态 | `INFO persistence` | `rdb_last_bgsave_status:ok` | 每周巡检 |
| Redis 内存使用 | `INFO memory` | `used_memory_rss < maxmemory` | 每周巡检 |
| Redis 主从延迟（若用主从） | `INFO replication` | `master_repl_offset` 跟得上 | 每周巡检 |
| 启动同步日志 | 应用日志 | 无 `Redis seq 启动同步失败` | 每次重启后 |
| `seq:invite_code` key 存在 | `redis-cli EXISTS seq:invite_code` | `(integer) 1`（已发过号后） | 每月巡检 |
| `seq:invite_code` 值合理 | `redis-cli GET seq:invite_code` | 与 DB `MAX(seq)` 接近（≥） | 每月巡检 |
| 容量预警 | `seq / 54^8` | `< 80%`（按当前用量极低，无需告警） | 季度评估 |

### 健康比对 SQL（每月巡检）

```sql
-- Redis seq 应 ≥ DB MAX(seq)，否则说明 Redis 落后，需触发应用重启或手动 syncSeqFromDb()
SELECT MAX(seq) AS db_max_seq, COUNT(*) AS code_count FROM sys_invite_code;
```

```bash
redis-cli GET seq:invite_code   # 期望：>= 上面的 db_max_seq
```

---

## 六、已知限制与应对

### 1. 运行时 Redis 完全不可用

**现象**：发号接口抛 `IllegalStateException("Redis INCR 返回 null，疑似连接异常")`

**影响**：无法生成新邀请码；已有的邀请码注册/分佣流程不受影响（只读 DB）

**应对**：
- 立即恢复 Redis 服务（重启 / 切主从）
- 应用启动时 `@PostConstruct` 会自动从 DB 同步 seq，恢复后无碰撞
- **未实现的运行时降级方案**（按需扩展）：可在 `nextSeq()` 中捕获 `RedisConnectionFailureException`，
  临时改用 `SELECT MAX(seq) FROM sys_invite_code + 1` 兜底，靠 `uk_seq` 唯一索引 + 重试防并发碰撞。
  当前未实现，因 DB 兜底方案并发性差且会拖慢响应，建议优先保障 Redis 可用性。

### 2. 多实例同时启动

**现象**：多个应用实例同时启动，都跑 `@PostConstruct`

**安全性**：Lua 脚本原子 compare-and-set 保证不会互相覆盖回退；最坏情况是多个实例都
SET 到同一个 DB_MAX 值，结果一致，无副作用。

### 3. Redis 主备切换期间号段空窗

**现象**：主备切换瞬间，从库的 `seq:invite_code` 可能落后主库几个 seq（异步复制）

**应对**：
- 切换后立即重启应用（触发 `@PostConstruct` 同步）
- 或调用 `syncSeqFromDb()` 手动同步
- DB 唯一索引 `uk_seq` 是最后防线，即便号段重叠也会拒绝插入，不会产生数据污染

### 4. 存量旧邀请码不可解码

**现象**：dev 阶段用旧方案（雪花 ID 取模 / 纯随机）生成的码，`decode()` 抛
`IllegalArgumentException`

**应对**：上线前清空 dev 数据（推荐），或参考"Step 1：数据库迁移"中的回填策略

---

## 七、关键文件索引

| 关注点 | 文件路径 |
|--------|---------|
| 发号器 + 启动同步 | `src/main/java/com/atguigu/meet/service/permission/invite/impl/RedisInviteSeqGenerator.java` |
| 编解码工具 | `src/main/java/com/atguigu/meet/utils/InviteCodeUtil.java` |
| 业务编排 | `src/main/java/com/atguigu/meet/service/permission/invite/impl/InviteCodeServiceImpl.java` |
| 实体（含 seq 字段） | `src/main/java/com/atguigu/meet/model/entity/permission/invite/SysInviteCode.java` |
| Mapper（含 selectMaxSeq） | `src/main/java/com/atguigu/meet/mapper/permission/invite/SysInviteCodeMapper.java` |
| 建表 SQL | `src/main/resources/sql/rbac.sql`（`sys_invite_code` 段） |
| Redis Bean 配置 | `src/main/java/com/atguigu/meet/config/RedisConfig.java` |

---

## 八、快速故障恢复 SOP

```
现象：用户报"邀请码生成失败"
  ↓
查应用日志，确认是否 Redis 异常
  ↓
是 Redis 异常 →
  1. 恢复 Redis（重启 / 切主从）
  2. 重启应用 或 调用 syncSeqFromDb() 触发同步
  3. 验证：redis-cli GET seq:invite_code ≥ SELECT MAX(seq) FROM sys_invite_code
  4. 让用户重试
  ↓
否（Redis 正常）→
  1. 查 sys_invite_code 是否已有该用户记录（uk_inviter 唯一索引）
  2. 查 seq 是否超容量上限（54^8 - 1）
  3. 查应用日志是否有其他异常堆栈
```
