package com.atguigu.meet.exception;

import com.atguigu.meet.common.Response;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * 全局异常处理器
 *
 * @Description 捕获所有 Controller 抛出的异常, 统一返回格式
 * @Date 2026-04-24 9:32
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * ===================== Controller 类上加了 @Validated 触发 =====================
     * 单个参数异常
     * public R test(
     *
     * @NotBlank(message = "名字不能为空")
     * @RequestParam String name
     * ) { }
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public Response<?> handleConstraintViolationException(ConstraintViolationException e) {
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        String msg = violations.stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .collect(Collectors.joining("; "));
        return Response.fail(400, msg);
    }

    // ===================== @Valid 表单/JSON参数校验异常 =====================
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Response<String> handleValidException(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldError().getDefaultMessage();
        log.warn("前端参数校验失败：{}", msg);
        return Response.fail(400, msg);
    }

    // ====================== 拦截 缺少@RequestBody / JSON格式错误 ======================
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Response<?> handleHttpMessageNotReadable(HttpMessageNotReadableException e) {
        return Response.fail(400, "请求参数不能为空，请传入正确的JSON格式参数");
    }

    // ====================== 捕获 405 异常 ======================
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Response<?> handleMethodNotAllowed(HttpRequestMethodNotSupportedException e) {
        return Response.fail(405, "请求方式不正确，请使用正确的请求方法（GET/POST/PUT/DELETE）");
    }

    // ===================== 业务异常 =====================
    @ExceptionHandler(BusinessException.class)
    public Response<String> handleBusinessException(BusinessException e) {
        log.warn("业务异常: {}", e.getMessage());
        return Response.fail(e.getCode(), e.getMessage());
    }

    // ===================== 404 接口不存在 / 路径参数缺失 =====================
    @ExceptionHandler(NoResourceFoundException.class)
    public Response<String> handleNoResourceFoundException(NoResourceFoundException e) {
        log.warn("404 接口不存在：{}", e.getMessage());
        return Response.fail(404, "接口不存在", "");
    }

    // ===================== 手动非法参数异常 =====================
    @ExceptionHandler(IllegalArgumentException.class)
    public Response<String> handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("参数异常：{}", e.getMessage());
        return Response.fail(e.getMessage());
    }

    // ===================== 空指针异常 =====================
    @ExceptionHandler(NullPointerException.class)
    public Response<String> handleNullPointerException(NullPointerException e) {
        log.error("空指针异常：", e);
        return Response.fail("数据不存在或为空");
    }

    // ===================== 全局兜底 所有未知系统异常 =====================
    @ExceptionHandler(Exception.class)
    public Response<String> handleException(Exception e) {
        log.error("系统异常: ", e);
        return Response.fail("服务器繁忙, 请稍后重试");
    }

}

















