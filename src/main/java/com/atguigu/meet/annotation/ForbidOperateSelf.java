package com.atguigu.meet.annotation;

import java.lang.annotation.*;

/**
 * @Description
 * @Date 2026-08-13 17:32
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ForbidOperateSelf {
    /**
     * 提示信息
     */
    String message() default "不允许操作当前登录账号";

    /**
     * DTO中ID字段名称，单个ID默认userId，数组传userIds
     */
    String idField() default "userId";
}
