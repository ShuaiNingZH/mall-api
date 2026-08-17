package com.atguigu.meet.model.entity.permission.user;

import lombok.Data;

import java.util.Set;

/**
 * @Description
 * @Date 2026-06-03 16:39
 */
@Data
public class AdminUser {
    private Long userId;
    private String phone;  // 管理员手机号
    private String username;
//    private Integer roleType; // 角色

    /**
     * 用户权限集合（从Redis/DB加载，存入ThreadLocal上下文）
     */
    private Set<String> permissions;
}