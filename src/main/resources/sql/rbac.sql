-- 1. 如果数据库不存在则创建，字符集utf8mb4（支持emoji），排序规则通用
CREATE DATABASE IF NOT EXISTS meet
DEFAULT CHARACTER SET utf8mb4
DEFAULT COLLATE utf8mb4_unicode_ci;

-- 2. 切换到刚创建的数据库
USE meet;
-- 3. RBAC 相关表（sys_user / sys_role / sys_user_role / sys_menu / sys_role_menu）
--    已统一迁移至 src/main/resources/sql/rbac.sql，执行 rbac.sql 即可
-- =============================================
-- RBAC 权限体系建表脚本
-- 表：sys_user / sys_role / sys_user_role / sys_menu / sys_role_menu
-- 关联关系：
--   sys_user  N<->N  sys_role        (通过 sys_user_role)
--   sys_role  N<->N  sys_menu        (通过 sys_role_menu)
--   sys_menu  自关联(parent_id) 形成目录-菜单-按钮三级树
-- 菜单类型：0目录 1菜单 2按钮权限
-- 外键：关联表(sys_user_role/sys_role_menu)均建立 FK，ON DELETE/UPDATE CASCADE；引擎统一 InnoDB
-- =============================================

-- 0. 用户表
CREATE TABLE IF NOT EXISTS sys_user (
    id          BIGINT       AUTO_INCREMENT PRIMARY KEY,
    username    VARCHAR(50)  NOT NULL UNIQUE COMMENT '用户名',
    password    VARCHAR(100) NOT NULL COMMENT '密码(BCrypt加密)',
    nickname    VARCHAR(50)  COMMENT '昵称',
    email       VARCHAR(100) COMMENT '邮箱',
    phone       VARCHAR(20)  COMMENT '手机号',
    age         TINYINT      COMMENT '年龄',
    gender      TINYINT      DEFAULT 0 COMMENT '性别 0未知 1男 2女',
    avatar      VARCHAR(255) COMMENT '头像图片地址',
    birthday    DATE         COMMENT '生日',
    status      TINYINT      DEFAULT 1 COMMENT '账号状态 0禁用 1正常',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted  TINYINT      DEFAULT 0 COMMENT '逻辑删除 0未删 1已删'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT '系统用户表';

-- 1. 角色表（权限分组载体：管理员、普通用户、运营等）
CREATE TABLE IF NOT EXISTS sys_role (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_name   VARCHAR(50)  NOT NULL COMMENT '角色名称',
    role_code   VARCHAR(50)  NOT NULL UNIQUE COMMENT '角色编码',
    status      TINYINT      DEFAULT 1 COMMENT '状态 1启用 0禁用',
    is_deleted     TINYINT      DEFAULT 0 COMMENT '逻辑删除',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT '系统角色表';

-- 2. 用户-角色关联表（多对多：一个用户多个角色，一个角色多个用户）
CREATE TABLE IF NOT EXISTS sys_user_role (
    id      BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID(sys_user.id)',
    role_id BIGINT NOT NULL COMMENT '角色ID(sys_role.id)',
    UNIQUE KEY uk_user_role (user_id, role_id),
    CONSTRAINT fk_user_role_user FOREIGN KEY (user_id) REFERENCES sys_user(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_user_role_role FOREIGN KEY (role_id) REFERENCES sys_role(id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT '用户角色关联表';

-- 3. 菜单/权限表（核心：存储菜单目录、页面菜单、操作按钮，按钮统一存本表）
--    通过 type 区分：0目录 1菜单 2按钮权限
--    通过 parent_id 自关联形成树形结构
CREATE TABLE IF NOT EXISTS sys_menu (
    id           BIGINT       AUTO_INCREMENT PRIMARY KEY,
    parent_id    BIGINT       DEFAULT 0 COMMENT '父菜单ID(0表示顶级；自关联外键未加,因0非有效id,如需可改NULL后添加)',
    menu_name    VARCHAR(50)  NOT NULL COMMENT '菜单/权限名称',
    menu_code    VARCHAR(100) COMMENT '菜单编码(目录/菜单可用，如 sys)',
    perms        VARCHAR(100) COMMENT '权限标识(按钮用，如 user:delete)',
    type         TINYINT      NOT NULL DEFAULT 1 COMMENT '类型 0目录 1菜单 2按钮权限',
    path         VARCHAR(200) COMMENT '路由路径(目录/菜单)',
    route_name   VARCHAR(100) COMMENT '路由名称(前端keep-alive匹配用,对应routeName)',
    component    VARCHAR(200) COMMENT '前端组件路径(菜单,对应componentPath)',
    icon         VARCHAR(100) COMMENT '图标',
    sort         INT          DEFAULT 0 COMMENT '排序(数字越小越靠前)',
    visible      TINYINT      DEFAULT 1 COMMENT '是否可见 1可见 0隐藏(语义等价hideInMenu,反向)',
    keep_alive   TINYINT      DEFAULT 0 COMMENT '是否缓存组件 1是 0否(对应keepAlive)',
    active_menu  VARCHAR(200) COMMENT '高亮菜单path(详情页等场景,对应activeMenu)',
    hide_in_tag  TINYINT      DEFAULT 0 COMMENT '是否在标签栏隐藏 1是 0否(对应hideInTag)',
    hide_parent  TINYINT      DEFAULT 0 COMMENT '是否隐藏父级菜单 1是 0否(对应hideParent)',
    status       TINYINT      DEFAULT 1 COMMENT '状态 1启用 0禁用',
    is_deleted      TINYINT      DEFAULT 0 COMMENT '逻辑删除',
    create_time  DATETIME     DEFAULT CURRENT_TIMESTAMP,
    update_time  DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT '系统菜单/权限表(目录-菜单-按钮统一管理)';

-- 4. 角色-菜单关联表（多对多：一个角色绑定多个菜单/按钮）
CREATE TABLE IF NOT EXISTS sys_role_menu (
    id      BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_id BIGINT NOT NULL COMMENT '角色ID(sys_role.id)',
    menu_id BIGINT NOT NULL COMMENT '菜单/权限ID(sys_menu.id)',
    UNIQUE KEY uk_role_menu (role_id, menu_id),
    CONSTRAINT fk_role_menu_role FOREIGN KEY (role_id) REFERENCES sys_role(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_role_menu_menu FOREIGN KEY (menu_id) REFERENCES sys_menu(id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT '角色菜单关联表';

-- =============================================
-- 初始化数据
-- =============================================

-- 超级管理员角色
INSERT IGNORE INTO sys_role(role_name, role_code) VALUES('超级管理员', 'SUPER_ADMIN');

-- 菜单数据：目录 -> 菜单 -> 按钮 三级结构
-- 系统管理目录
INSERT IGNORE INTO sys_menu(id, parent_id, menu_name, menu_code, perms, type, path, component, icon, sort, visible) VALUES
(1, 0, '系统管理', 'sys',  NULL,        0, '/sys',       NULL,                     'Setting', 10, 1),
-- 用户管理菜单
(2, 1, '用户管理', 'user', NULL,        1, 'user',       'sys/user/index',         'User',     10, 1),
-- 用户管理下的按钮权限
(3, 2, '用户查询', NULL,   'user:query',  2, NULL, NULL, NULL, 1, 1),
(4, 2, '用户新增', NULL,   'user:add',    2, NULL, NULL, NULL, 2, 1),
(5, 2, '用户修改', NULL,   'user:update', 2, NULL, NULL, NULL, 3, 1),
(6, 2, '用户删除', NULL,   'user:delete', 2, NULL, NULL, NULL, 4, 1);

-- 给超级管理员分配以上全部菜单/权限
INSERT IGNORE INTO sys_role_menu(role_id, menu_id)
SELECT 1, id FROM sys_menu;

-- =============================================
-- 初始化用户数据（密码均为 BCrypt 加密，原始密码见注释）
-- =============================================
INSERT IGNORE INTO sys_user (id, username, password, nickname, email, phone, age, gender, avatar, birthday, status, create_time, update_time, is_deleted) VALUES
-- 密码: 123456
(1, '176395248881', '$2b$10$JnMlXJG65NApREMFbecz/OOavrH8cptEARJQhKjCEzNNoU5H/WJUW', '哈哈哈', 'AbC123@example.com', '17639524881', 18, 0, '/upload/avatar/7', '1989-05-26', 1, '2026-08-13 16:06:13', '2026-08-13 16:13:16', 0),
-- 密码: admin
(2, '13823456789', '$2b$10$nA8pOSsQ4.Hvu9w1K1Il8e5LOCjmqdP9IjWtZhHCoAaQshfaynGFi', '测试数据', 'x7ZzQ9@test.com', '13823456789', 18, 2, NULL, '1999-04-01', 1, '2026-08-13 16:08:13', '2026-08-13 16:13:16', 0),
-- 密码: 111111
(3, '15987654321', '$2b$10$wTQRVgVrAG4EL/RcvD07EuPSgNubDR8osB1cxyPkm74LS2nL8jXSe', '张雨晴', 'rR2kTSu@demo.com', '15987654321', 20, 1, NULL, '2000-05-10', 1, '2026-08-13 16:08:13', '2026-08-13 16:13:16', 0),
-- 密码: 000000
(4, '18712345678', '$2b$10$LP.i0aMaRJCbTj4uOCl7ru47sRHcqvB6BUa9.77ElXJxXJv5oLCHG', '刘浩然', 'bNGzH2@mail.cc', '18712345678', 25, 1, NULL, '2008-05-15', 1, '2026-08-13 16:08:13', '2026-08-13 16:13:16', 0),
-- 密码: password
(5, '13698765432', '$2b$10$F1/AXnsxWLLKk4ueiwMGcumt2HO0P4WS2o44un9li9m4lGez8HlPu', '陈佳', 'P7q1zS@abc.com', '13698765432', 19, 2, NULL, '1991-05-15', 1, '2026-08-13 16:08:13', '2026-08-13 16:13:16', 0),
-- 密码: test
(6, '15523458769', '$2b$10$xn/n4bpSBq03Y4OZpEvDIeQIHPd.hqNAt9kmlkn0RFw7vKynVP5ke', '赵天宇', 'mK5dF8H@serv.com', '15523458769', 23, 1, NULL, '2014-05-08', 2, '2026-08-13 16:08:13', '2026-08-13 16:13:16', 0),
-- 密码: 123456
(7, '13945678912', '$2b$10$JnMlXJG65NApREMFbecz/OOavrH8cptEARJQhKjCEzNNoU5H/WJUW', '周俊伟', 'Z9xC7vB@work.com', '13945678912', 26, 1, NULL, '2000-05-18', 1, '2026-08-13 16:08:13', '2026-08-13 16:13:16', 0),
-- 密码: admin
(8, '17639528888', '$2b$10$nA8pOSsQ4.Hvu9w1K1Il8e5LOCjmqdP9IjWtZhHCoAaQshfaynGFi', '李白', NULL, '17639528888', 18, 1, NULL, NULL, 1, '2026-08-13 16:08:13', '2026-08-13 16:13:16', 0),
-- 密码: 111111
(9, '17639528888_2', '$2b$10$wTQRVgVrAG4EL/RcvD07EuPSgNubDR8osB1cxyPkm74LS2nL8jXSe', '李白', NULL, '17639528888', 18, 2, NULL, NULL, 2, '2026-08-13 16:08:13', '2026-08-13 16:13:16', 0),
-- 密码: 000000
(10, '17639528888_3', '$2b$10$LP.i0aMaRJCbTj4uOCl7ru47sRHcqvB6BUa9.77ElXJxXJv5oLCHG', '李白', NULL, '17639528888', 18, 2, NULL, NULL, 2, '2026-08-13 16:08:13', '2026-08-13 16:13:16', 0),
-- 密码: password
(11, '11', '$2b$10$F1/AXnsxWLLKk4ueiwMGcumt2HO0P4WS2o44un9li9m4lGez8HlPu', '李白', NULL, NULL, 18, 2, NULL, NULL, 2, '2026-08-13 16:08:13', '2026-08-13 16:13:16', 0);

-- =============================================
-- 给指定用户绑定超级管理员角色
-- =============================================
INSERT IGNORE INTO sys_user_role(user_id, role_id) VALUES(1, 1);
