package com.atguigu.meet.exception;

/**
 * @Description
 * @Date 2026-04-24 10:42
 */
public class BusinessException extends RuntimeException {
    private Integer code;

    public BusinessException(String msg) {
        super(msg);
        this.code = 500;
    }

    public BusinessException(Integer code, String msg) {
        super(msg);
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }
}
