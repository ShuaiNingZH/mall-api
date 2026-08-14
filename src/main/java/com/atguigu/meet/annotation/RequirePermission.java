package com.atguigu.meet.annotation;

import java.lang.annotation.*;

/**
 * 权限校验注解
 * <p>
 * 使用方式：
 * 1. 标记在 Controller 方法上，指定访问该接口所需的权限标识
 * 2. 支持 AND / OR 两种逻辑模式
 * <p>
 * 示例：
 * <pre>
 * // 需要 user:list 权限
 * &#64;RequirePermission("user:list")
 *
 * // 需要同时拥有 user:add 和 user:list 两个权限
 * &#64;RequirePermission(value = {"user:add", "user:list"}, mode = RequirePermission.Mode.AND)
 *
 * // 拥有 user:delete 或 admin 任意一个权限即可
 * &#64;RequirePermission(value = {"user:delete", "admin"}, mode = RequirePermission.Mode.OR)
 * </pre>
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequirePermission {

    /**
     * 权限标识列表（如 "user:delete", "sys:role:add"）
     */
    String[] value() default {};

    /**
     * 校验模式：
     * - AND：必须拥有所有权限（默认）
     * - OR：拥有任意一个权限即可
     */
    Mode mode() default Mode.AND;

    /**
     * 自定义权限不足时的提示信息
     */
    String message() default "无访问权限";

    /**
     * 逻辑模式枚举
     */
    enum Mode {
        /** 与：必须拥有全部权限 */
        AND,
        /** 或：拥有任意一个权限即可 */
        OR
    }
}
