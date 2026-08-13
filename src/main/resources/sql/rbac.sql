-- =============================================
-- RBAC 权限体系建表脚本
-- 表：sys_user / sys_role / sys_user_role / sys_permission / sys_role_permission
-- =============================================

-- 0. 用户表
CREATE TABLE IF NOT EXISTS  sys_user (
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
) COMMENT '系统用户表';

-- 1. 角色表
DROP TABLE IF EXISTS sys_role;
CREATE TABLE sys_role (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_name   VARCHAR(50)  NOT NULL COMMENT '角色名称',
    role_code   VARCHAR(50)  NOT NULL UNIQUE COMMENT '角色编码',
    status      TINYINT      DEFAULT 1 COMMENT '状态 1启用 0禁用',
    deleted     TINYINT      DEFAULT 0 COMMENT '逻辑删除',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT '系统角色表';

-- 2. 用户-角色关联表
DROP TABLE IF EXISTS sys_user_role;
CREATE TABLE sys_user_role (
    id      BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID(sys_user.id)',
    role_id BIGINT NOT NULL COMMENT '角色ID(sys_role.id)',
    UNIQUE KEY uk_user_role (user_id, role_id)
) COMMENT '用户角色关联表';

-- 3. 权限表（权限码粒度，如 user:delete）
DROP TABLE IF EXISTS sys_permission;
CREATE TABLE sys_permission (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    parent_id       BIGINT       DEFAULT 0 COMMENT '父权限ID',
    permission_name VARCHAR(50)  NOT NULL COMMENT '权限名称',
    permission_code VARCHAR(100) NOT NULL UNIQUE COMMENT '权限编码 如 user:delete',
    type            TINYINT      DEFAULT 1 COMMENT '类型 1菜单 2按钮',
    status          TINYINT      DEFAULT 1 COMMENT '状态 1启用 0禁用',
    deleted         TINYINT      DEFAULT 0 COMMENT '逻辑删除',
    create_time     DATETIME     DEFAULT CURRENT_TIMESTAMP,
    update_time     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT '系统权限表';

-- 4. 角色-权限关联表
DROP TABLE IF EXISTS sys_role_permission;
CREATE TABLE sys_role_permission (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_id       BIGINT NOT NULL COMMENT '角色ID',
    permission_id BIGINT NOT NULL COMMENT '权限ID',
    UNIQUE KEY uk_role_permission (role_id, permission_id)
) COMMENT '角色权限关联表';

-- =============================================
-- 初始化数据
-- =============================================

-- 超级管理员角色
INSERT INTO sys_role(role_name, role_code) VALUES('超级管理员', 'SUPER_ADMIN');

-- 用户管理相关权限码
INSERT INTO sys_permission(permission_name, permission_code, type) VALUES
('用户查询', 'user:query',  1),
('用户新增', 'user:add',    2),
('用户修改', 'user:update', 2),
('用户删除', 'user:delete', 2);

-- 给超级管理员分配以上全部权限
INSERT INTO sys_role_permission(role_id, permission_id)
SELECT 1, id FROM sys_permission;

-- =============================================
-- 初始化用户数据（密码均为 BCrypt 加密，原始密码见注释）
-- =============================================
INSERT INTO sys_user (id, username, password, nickname, email, phone, age, gender, avatar, birthday, status, create_time, update_time, is_deleted) VALUES
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
INSERT INTO sys_user_role(user_id, role_id) VALUES(1, 1);
