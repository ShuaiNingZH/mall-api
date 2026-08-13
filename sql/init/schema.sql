-- 1. 如果数据库不存在则创建，字符集utf8mb4（支持emoji），排序规则通用
CREATE DATABASE IF NOT EXISTS meet
DEFAULT CHARACTER SET utf8mb4
DEFAULT COLLATE utf8mb4_unicode_ci;

-- 2. 切换到刚创建的数据库
USE meet;

-- 3. RBAC 相关表（sys_user / sys_role / sys_user_role / sys_permission / sys_role_permission）
--    已统一迁移至 src/main/resources/sql/rbac.sql，执行 rbac.sql 即可

