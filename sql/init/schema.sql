-- 1. 如果数据库不存在则创建，字符集utf8mb4（支持emoji），排序规则通用
CREATE DATABASE IF NOT EXISTS meet
DEFAULT CHARACTER SET utf8mb4
DEFAULT COLLATE utf8mb4_unicode_ci;

-- 2. 切换到刚创建的数据库
USE meet;

-- 3. 创建用户表
CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    nickname VARCHAR(50) COMMENT '昵称',
    email VARCHAR(100) COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '手机号',
    age TINYINT COMMENT '年龄',
    gender TINYINT DEFAULT 0 COMMENT '性别 0未知 1男 2女',
    avatar VARCHAR(255) COMMENT '头像图片地址',
    birthday DATE COMMENT '生日',
    status TINYINT DEFAULT 1 COMMENT '账号状态 0禁用 1正常',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT DEFAULT 0 COMMENT '逻辑删除 0未删 1已删'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

