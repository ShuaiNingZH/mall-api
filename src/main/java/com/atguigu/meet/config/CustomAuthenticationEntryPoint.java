package com.atguigu.meet.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @Description
 * @Date 2026-05-19 10:50
 */
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException, ServletException {
        // 只处理 AuthenticationException 及其子类
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(401);
        String message = switch (authException.getClass().getSimpleName()) {
            case "ExpiredJwtException" -> "令牌已过期";
            case "JwtException" -> "令牌格式错误";
            case "AuthenticationServiceException" -> authException.getMessage();
            default -> "未授权的访问";
        };
        response.getWriter().write(
                String.format("{\"code\":401,\"msg\":\"%s\",\"data\":%s}", message, null)
        );
    }
}
