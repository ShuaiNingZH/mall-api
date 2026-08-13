-- =============================================
-- RBAC 权限体系建表脚本
-- 表：sys_role / sys_user_role / sys_permission / sys_role_permission
-- =============================================

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
-- 给指定用户绑定超级管理员角色（把下面的 1 换成 sys_user 里你要设为超管的用户ID）
-- =============================================
-- INSERT INTO sys_user_role(user_id, role_id) VALUES(1, 1);
