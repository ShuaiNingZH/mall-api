-- 11. 抢购场次表（一个活动下有多场抢购场次）
-- 注：在用户提供的原表结构基础上补充 is_deleted 字段，对齐项目逻辑删除规范（@TableLogic）
CREATE TABLE IF NOT EXISTS `t_session` (
    `id`                   BIGINT       AUTO_INCREMENT PRIMARY KEY COMMENT '场次主键ID',
    `session_name`         VARCHAR(64)  NOT NULL COMMENT '场次名称：上午场/下午场',
    `session_status`       TINYINT      NOT NULL DEFAULT 1 COMMENT '场次状态 1开启 0关闭',
    `enter_control_minute` INT          NOT NULL DEFAULT 0 COMMENT '进场时间控制(分钟)',
    `rush_start_time`      DATETIME     NOT NULL COMMENT '抢购开始时间（完整年月日时分秒，例：2026-08-18 09:50:00）',
    `rush_end_time`        DATETIME     NOT NULL COMMENT '抢购结束时间（完整年月日时分秒，例：2026-08-18 17:00:00）',
    `max_buy_count`        INT          NOT NULL DEFAULT 1 COMMENT '最多购买次数(次)',
    `before_forbid_minute` INT          NOT NULL DEFAULT 0 COMMENT '开场前禁止委托时间(分钟)',
    `after_forbid_minute`  INT          NOT NULL DEFAULT 0 COMMENT '结束后禁止委托时间(分钟)',
    `bg_img`               VARCHAR(255) DEFAULT '' COMMENT '场次背景图地址',
    `sort`                 INT          NOT NULL DEFAULT 0 COMMENT '排序号（用来前端按顺序展示第1场、第2场）',
    `is_deleted`           TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除 0未删 1已删',
    `create_time`          DATETIME     DEFAULT CURRENT_TIMESTAMP,
    `update_time`          DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY `idx_status_deleted` (`session_status`, `is_deleted`) COMMENT '查询启用场次联合索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='抢购场次表';

-- =============================================
-- 抢购场次模块菜单数据
-- =============================================
-- 抢购场次管理菜单 (id 从 50 开始，避免与已有菜单冲突)
INSERT IGNORE INTO sys_menu(id, parent_id, name, menu_code, perm, type, path, component_path, icon, sort, visible) VALUES
(50, 1,  '抢购场次管理', 'session', NULL,                  1, 'session',  'session/index',  'Clock',  50, 1),
(51, 50, '场次查询',     NULL,     'session:query',          2, NULL, NULL, NULL, 1, 1),
(52, 50, '场次新增',     NULL,     'session:add',            2, NULL, NULL, NULL, 2, 1),
(53, 50, '场次修改',     NULL,     'session:update',         2, NULL, NULL, NULL, 3, 1),
(54, 50, '场次删除',     NULL,     'session:delete',         2, NULL, NULL, NULL, 4, 1);

-- 给超级管理员分配抢购场次管理菜单/权限
INSERT IGNORE INTO sys_role_menu(role_id, menu_id)
SELECT 1, id FROM sys_menu WHERE id IN (50, 51, 52, 53, 54);

-- =============================================
-- 抢购托售商品模块表：t_consign_goods
-- =============================================

-- 12. 抢购托售商品主表（对应编辑页 + 列表）
CREATE TABLE IF NOT EXISTS `t_consign_goods` (
    `id`            BIGINT        AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    `goods_name`    VARCHAR(255)  COMMENT '抢购区商品名称',
    `goods_price`   DECIMAL(10,2) COMMENT '抢购区商品价格',
    `member_id`     BIGINT        COMMENT '本轮委托人ID，关联sys_user',
    `session_id`    BIGINT        COMMENT '所属场次ID，关联t_session',
    `cover_img`     VARCHAR(500)  COMMENT '商品缩略图url',
    `detail_img`    VARCHAR(500)  COMMENT '商品详情图url',
    `goods_detail`  TEXT          COMMENT '商品详情富文本',
    `sale_times`    INT           DEFAULT 0 COMMENT '委托售卖次数',
    `goods_status`  TINYINT       COMMENT '商品业务状态 1挂卖中 2已抢购待付款 3等待确认付款 4待处理 5委托代卖 6委托发货',
    `online_status` TINYINT       DEFAULT 0 COMMENT '0下架 1上架',
    `is_deleted`    TINYINT       DEFAULT 0 COMMENT '假删除 0正常 1删除',
    `create_time`   DATETIME      DEFAULT CURRENT_TIMESTAMP,
    `update_time`   DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY `idx_member` (`member_id`) COMMENT '按委托人查询索引',
    KEY `idx_session` (`session_id`) COMMENT '按场次查询索引',
    KEY `idx_goods_status` (`goods_status`) COMMENT '按业务状态查询索引',
    KEY `idx_online_status` (`online_status`, `is_deleted`) COMMENT '查询上架未删除商品联合索引',
    CONSTRAINT `fk_consign_goods_member` FOREIGN KEY (`member_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_consign_goods_session` FOREIGN KEY (`session_id`) REFERENCES `t_session` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='抢购托售商品主表';

-- =============================================
-- 抢购托售商品模块菜单数据
-- =============================================
-- 托售商品管理菜单 (id 从 60 开始，避免与已有菜单冲突)
INSERT IGNORE INTO sys_menu(id, parent_id, name, menu_code, perm, type, path, component_path, icon, sort, visible) VALUES
(60, 1,  '托售商品管理', 'consign', NULL,                       1, 'consign',  'goods/consign/index',  'ShoppingBag', 60, 1),
(61, 60, '托售商品查询', NULL,     'goods:consign:query',        2, NULL, NULL, NULL, 1, 1),
(62, 60, '托售商品新增', NULL,     'goods:consign:add',          2, NULL, NULL, NULL, 2, 1),
(63, 60, '托售商品修改', NULL,     'goods:consign:update',       2, NULL, NULL, NULL, 3, 1),
(64, 60, '托售商品删除', NULL,     'goods:consign:delete',       2, NULL, NULL, NULL, 4, 1),
(65, 60, '托售商品上下架', NULL,   'goods:consign:shelf',        2, NULL, NULL, NULL, 5, 1),
(66, 60, '托售商品业务状态流转', NULL, 'goods:consign:biz:status', 2, NULL, NULL, NULL, 6, 1);

-- 给超级管理员分配托售商品管理菜单/权限
INSERT IGNORE INTO sys_role_menu(role_id, menu_id)
SELECT 1, id FROM sys_menu WHERE id IN (60, 61, 62, 63, 64, 65, 66);
