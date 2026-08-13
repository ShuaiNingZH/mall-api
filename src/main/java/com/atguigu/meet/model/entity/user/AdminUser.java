package com.atguigu.meet.model.entity.user;

import lombok.Data;

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
}
