package com.atguigu.meet.config;

import com.atguigu.meet.filter.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

/**
 * @Description
 * @Date 2026-05-18 14:19
 */
@Configuration
@EnableWebSecurity // 开启 Spring Security 安全功能
@EnableMethodSecurity // 开启方法级权限控制（@PreAuthorize 生效）
@Slf4j
public class SecurityConfig {
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private CorsConfigurationSource corsConfigurationSource;

    @Autowired
    private JwtSecurityProperties jwtSecurityProperties;

    @Autowired
    private CustomAuthenticationEntryPoint authenticationEntryPoint;

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, accessDeniedException) -> {
            if (!response.isCommitted()) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write("{\"code\":403,\"message\":\"权限不足：" + accessDeniedException.getMessage() + "\"}");
            } else {
                log.error("Access denied after response committed: {}", accessDeniedException.getMessage());
            }
        };
    }

    /**
     * 配置请求拦截规则
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. 关闭跨站请求伪造, 前后端分离必须关
                .csrf(csrf -> csrf.disable())
                // 启用 CORS 并配置（替换默认的 CorsFilter）
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                // 2. 设置无状态会话, JWT 不用 session
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 3. 安全上下文（JWT 必须：自动保存）
                .securityContext(context -> context.requireExplicitSave(false))
                // 4. 异常处理统一管理安全异常（401/403 自定义 JSON 返回）
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler())
                )
                // 5. 添加 JWT 过滤器，将自定义的认证过滤器执行优先级置于授权之前
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                // 6. 配置接口放行/拦截规则
                .authorizeHttpRequests(auth -> auth
                        // 所有人都能访问（不用登录）
                        .requestMatchers(
                                jwtSecurityProperties
                                        .getPublicPaths()
                                        .toArray(String[]::new)
                        ).permitAll()
                        // 其他所有接口 -> 必须登录才能访问
                        .anyRequest().authenticated()
                );
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

















