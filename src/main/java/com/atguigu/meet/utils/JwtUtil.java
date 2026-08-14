package com.atguigu.meet.utils;

import com.atguigu.meet.service.auth.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Description
 * @Date 2026-05-15 14:21
 */
public class JwtUtil implements JwtService {
    // ====================== 常量写在这里 ======================
    // base64 秘钥
    private final String SECRET_KEY_STR;
    // 过期时间 2 小时
    private final long EXPIRE_TIME;
    // 解密 base64
    private final SecretKey SECRET_KEY;

    public JwtUtil(String SECRET_KEY_STR, Long EXPIRE_TIME) {
        byte[] keyBytes = Base64.getDecoder().decode(SECRET_KEY_STR);
        this.SECRET_KEY = Keys.hmacShaKeyFor(keyBytes);
        this.SECRET_KEY_STR = SECRET_KEY_STR;
        this.EXPIRE_TIME = EXPIRE_TIME;
    }

    // 仅支持密钥的构造函数, 默认1小时
    public JwtUtil(String SECRET_KEY) {
        this(SECRET_KEY, 3600000L);
    }

    /**
     * 生成 JWT 令牌
     *
     * @param userId 用户ID
     * @return 生成的 JWT 令牌
     */
    public String generateToken(
            Long userId,
            String username,
            String phone,
            Map<String, Object> claims
    ) {
        if (username != null) {
            claims.put("username", username);
        }
        if (phone != null) {
            claims.put("phone", phone);
        }
        return generateToken(userId, claims);
    }

    public String generateToken(
            Long userId,
            Map<String, Object> claims
    ) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userId.toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE_TIME))
                .signWith(SECRET_KEY)
                .compact();
    }

    public Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(SECRET_KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // 解析 token, 获取用户 id
    public Long extractUserId(String token) {
        final Claims claims = extractClaims(token);
        return Long.valueOf(claims.getSubject());
    }

    // 解析 token, 获取手机号
    public String extractPhone(String token) {
        final Claims claims = extractClaims(token);
        return claims.get("phone", String.class);
    }

    // 解析 token, 获取用户名
    public String extractUsername(String token) {
        final Claims claims = extractClaims(token);
        return claims.get("username", String.class);
    }

    // 解析 token, 获取权限码列表
    @SuppressWarnings("unchecked")
    public List<String> extractPermissions(String token) {
        final Claims claims = extractClaims(token);
        Object perms = claims.get("permissions");
        if (perms instanceof List) {
            return (List<String>) perms;
        }
        return new java.util.ArrayList<>();
    }

    /**
     * 验证JWT的有效性
     *
     * @param token JWT令牌
     * @return JWT是否有效
     */
    public boolean isTokenValid(String token) {
        try {
            Claims claims = extractClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}




























