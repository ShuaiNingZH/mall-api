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
    `online_status` TINYINT       DEFAULT 0 COMMENT '0待上架 1上架',
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

-- 13. 抢购托售商品操作日志表（记录新增/编辑/删除/上下架/业务状态流转，content 存变更内容JSON: {before,after,changedFields,remark}）
-- 注：日志表不建外键，避免级联删除丢失审计记录、保留完整历史
CREATE TABLE IF NOT EXISTS `t_consign_goods_operate_log` (
    `id`           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '日志ID',
    `consign_goods_id` BIGINT UNSIGNED NOT NULL COMMENT '托售商品ID(t_consign_goods.id)',
    `admin_id`     BIGINT UNSIGNED NOT NULL COMMENT '操作管理员ID(sys_user.id)',
    `operate_type` TINYINT         NOT NULL COMMENT '操作类型 1新增 2编辑 3删除 4上下架 5业务状态流转',
    `operate_desc` VARCHAR(255)    DEFAULT NULL COMMENT '操作中文描述(如:新增/编辑/删除/上下架/状态流转:挂卖中->待付款)，列表展示用，避免每次解析JSON',
    `from_status`  TINYINT         DEFAULT NULL COMMENT '业务流转前状态(仅operate_type=5有效 1挂卖中 2已抢购待付款 3等待确认付款 4待处理 5委托代卖 6委托发货)',
    `to_status`    TINYINT         DEFAULT NULL COMMENT '业务流转后状态(仅operate_type=5有效)',
    `ip`           VARCHAR(50)     DEFAULT NULL COMMENT '操作人客户端IP，溯源定位操作来源',
    `user_agent`   VARCHAR(500)    DEFAULT NULL COMMENT '操作人浏览器/客户端设备信息，安全审计用',
    `content`      TEXT            DEFAULT NULL COMMENT '变更内容JSON，格式: {"before":{...},"after":{...},"changedFields":["xxx"],"remark":"状态流转:挂卖中->待付款"}，before/after为前后快照',
    `create_time`  DATETIME        DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    PRIMARY KEY (`id`),
    KEY `idx_consign_goods_id` (`consign_goods_id`) COMMENT '按托售商品查询操作记录索引',
    KEY `idx_admin_id` (`admin_id`) COMMENT '按操作人查询索引',
    KEY `idx_create_time` (`create_time`) COMMENT '按操作时间查询',
    KEY `idx_biz_flow` (`to_status`, `from_status`) COMMENT '业务状态流转查询索引(按目标状态+源状态)'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='抢购托售商品操作日志表';

CREATE TABLE IF NOT EXISTS `t_goods` (
    `id`             BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `goods_name`     VARCHAR(255)    NOT NULL DEFAULT '' COMMENT '商品名称',
    `category_name`  VARCHAR(128)    NOT NULL DEFAULT '' COMMENT '商品种类名称',
    `goods_sn`       VARCHAR(64)     NOT NULL DEFAULT '' COMMENT '商品货号/编码，唯一',
    `goods_thumb`    VARCHAR(512)    NOT NULL DEFAULT '' COMMENT '商品缩略图URL',
    `price`          DECIMAL(12,2)   NOT NULL DEFAULT 0.00 COMMENT '商品售价',
    `stock`          INT UNSIGNED    NOT NULL DEFAULT 0 COMMENT '库存数量',
    `sales`          INT UNSIGNED    NOT NULL DEFAULT 0 COMMENT '销量',
    `status`         TINYINT         NOT NULL DEFAULT 0 COMMENT '商品状态 0=待上架 1=已上架',
    `is_deleted`     TINYINT         NOT NULL DEFAULT 0 COMMENT '逻辑删除 0未删 1已删',
    `create_time`    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
    `update_time`    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by`      BIGINT          DEFAULT NULL COMMENT '创建人ID(管理员id)',
    `update_by`      BIGINT          DEFAULT NULL COMMENT '更新人ID',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_goods_sn` (`goods_sn`) COMMENT '货号唯一索引',
    KEY `idx_status_deleted` (`status`, `is_deleted`) COMMENT '查询已上架未删除商品联合索引',
    KEY `idx_category_name` (`category_name`) COMMENT '按商品种类查询索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

-- 10. 商品操作日志表（记录新增/编辑/删除/上下架行为，content 存变更内容JSON: {before,after,changedFields,remark}）
CREATE TABLE IF NOT EXISTS `t_goods_operate_log` (
    `id`           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '日志ID',
    `goods_id`     BIGINT UNSIGNED NOT NULL COMMENT '商品ID(t_goods.id)',
    `admin_id`     BIGINT UNSIGNED NOT NULL COMMENT '操作管理员ID(sys_user.id)',
    `operate_type` TINYINT         NOT NULL COMMENT '操作类型 1新增 2编辑 3删除 4上下架',
    `operate_desc` VARCHAR(255)    DEFAULT NULL COMMENT '操作中文描述(如:新增商品/编辑商品/删除商品/上下架)，列表展示用，避免每次解析JSON',
    `ip`           VARCHAR(50)     DEFAULT NULL COMMENT '操作人客户端IP，溯源定位操作来源',
    `user_agent`   VARCHAR(500)    DEFAULT NULL COMMENT '操作人浏览器/客户端设备信息，安全审计用',
    `content`      TEXT            DEFAULT NULL COMMENT '变更内容JSON，格式: {"before":{...},"after":{...},"changedFields":["xxx"],"remark":"编辑商品基础信息"}，before/after为前后快照',
    `create_time`  DATETIME        DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    PRIMARY KEY (`id`),
    KEY `idx_goods_id` (`goods_id`) COMMENT '按商品查询操作记录索引',
    KEY `idx_admin_id` (`admin_id`) COMMENT '按操作人查询索引',
    KEY `idx_create_time` (`create_time`) COMMENT '按操作时间查询'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品操作日志表';
