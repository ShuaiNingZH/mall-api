package com.atguigu.meet.utils;

import com.atguigu.meet.model.entity.user.AdminUser;

/**
 * @Description
 * @Date 2026-06-03 16:40
 */
public class AdminContext {
    private static final ThreadLocal<AdminUser> ADMIN_TL = new ThreadLocal<>();

    // 过滤器存入用户
    public static void set(AdminUser user) {
        ADMIN_TL.set(user);
    }

    // Controller/Service 获取登录管理员
    public static AdminUser get() {
        return ADMIN_TL.get();
    }

    // 请求结束清理，防止内存泄漏
    public static void remove() {
        ADMIN_TL.remove();
    }

    // 获取当前登录管理员ID
    public static Long getLoginUserId() {
        AdminUser adminUser = ADMIN_TL.get();
        if (adminUser == null) {
            return null;
        }
        return adminUser.getUserId();
    }
}
