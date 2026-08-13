package com.atguigu.meet.service.auth;

/**
 * @Description
 * @Date 2026-05-19 10:10
 */
public interface JwtService {
    Long extractUserId(String token);

    String extractPhone(String token);

    String extractUsername(String token);

    boolean isTokenValid(String token);
}
