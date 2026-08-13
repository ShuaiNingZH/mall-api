package com.atguigu.meet.filter;

import com.atguigu.meet.config.JwtSecurityProperties;
import com.atguigu.meet.model.entity.user.AdminUser;
import com.atguigu.meet.utils.AdminContext;
import com.atguigu.meet.utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.util.PathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description 解析Token、认证用户
 * @Date 2026-05-18 16:38
 */
@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private JwtSecurityProperties jwtSecurityProperties;

    @Autowired
    private PathMatcher pathMatcher;

    @Autowired
    private AuthenticationEntryPoint authenticationEntryPoint;

    private String getTokenFormRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        // ====================== 1. 判断接口 uri 是否无需 token, 无 token 直接放行, 交给 Security 拦截  ======================
        String uri = request.getRequestURI();
        if (jwtSecurityProperties
                .getPublicPaths()
                .stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, uri))
        ) {
            filterChain.doFilter(request, response);
            return;
        }
        // ====================== 2. 需要 token  ======================
        String token = getTokenFormRequest(request);
        log.info("[JWT] Processing request - URI: {}, Token: {}", uri, token);
        if (token != null && !"undefined".equals(token)) {
            try {
                // ====================== 3. 校验 Token 是否合法 ======================
                // 自己写的工具类：验证签名、过期时间、格式
                boolean isValid = jwtUtil.isTokenValid(token);
                if (!isValid) {
                    authenticationEntryPoint.commence(request, response, new AuthenticationServiceException("令牌无效"));
                    return;
                }
                // 4. 解析 token 获取用户 userId
                Long userId = jwtUtil.extractUserId(token);
                String phone = jwtUtil.extractPhone(token);
                String username = jwtUtil.extractUsername(token);
                // 解析权限码列表，构建 Spring Security 的授权信息
                List<String> permissions = jwtUtil.extractPermissions(token);
                List<GrantedAuthority> authorities = permissions.stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());
                HashMap<String, Object> userinfo = new HashMap<>();
                userinfo.put("userId", userId);
                userinfo.put("phone", phone);
                userinfo.put("username", username);
                // 5. 构建认证信息，告诉 Spring Security：这个人已登录！并携带其权限
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userinfo,
                        null,
                        authorities
                );
                // 6. 存入上下文
                SecurityContextHolder.getContext().setAuthentication(authentication);

                // 存入用户信息上下文
                AdminUser adminUser = new AdminUser();
                adminUser.setUserId(userId);
                adminUser.setPhone(phone);
                adminUser.setUsername(username);
                AdminContext.set(adminUser);
            } catch (Exception ex) {
                // 任何异常都清空认证信息，避免上下文泄漏
                SecurityContextHolder.clearContext();
                AdminContext.remove();
                authenticationEntryPoint.commence(request, response, new AuthenticationServiceException("令牌验证失败: " + ex.getMessage()));
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}

















