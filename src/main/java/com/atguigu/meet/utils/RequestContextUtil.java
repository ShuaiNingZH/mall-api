package com.atguigu.meet.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * HTTP 请求上下文工具类
 * <p>
 * 在 Service 层直接获取当前线程的 HttpServletRequest（无需 Controller 显式传参），
 * 用于操作日志记录客户端 IP / User-Agent 等审计信息。
 * <p>
 * 原理：Spring 通过 RequestContextHolder + ThreadLocal 在请求线程内绑定请求对象，
 * 由 Spring MVC 在 DispatcherServlet 进入时设置，请求结束自动清理。
 */
public class RequestContextUtil {

    private RequestContextUtil() {
    }

    /**
     * 获取当前线程的 HttpServletRequest
     *
     * @return 当前请求；非 Web 线程（如定时任务、异步线程）返回 null
     */
    public static HttpServletRequest getRequest() {
        RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
        if (!(attrs instanceof ServletRequestAttributes servletAttrs)) {
            return null;
        }
        return servletAttrs.getRequest();
    }

    /**
     * 获取客户端真实 IP（穿透常见反向代理头）
     * <p>
     * 优先级：X-Forwarded-For 首个 IP → X-Real-IP → Proxy-Client-IP → WL-Client-IP → remoteAddr
     * <p>
     * 注意：X-Forwarded-For 可被客户端伪造，仅作审计参考；如需强可信需在网关层覆写该头。
     *
     * @return 客户端 IP；获取不到返回 null
     */
    public static String getClientIp() {
        HttpServletRequest request = getRequest();
        if (request == null) {
            return null;
        }
        String ip = request.getHeader("X-Forwarded-For");
        if (StringUtils.hasText(ip) && !"unknown".equalsIgnoreCase(ip)) {
            // X-Forwarded-For 格式：client, proxy1, proxy2，取第一个非空
            int comma = ip.indexOf(',');
            if (comma > 0) {
                ip = ip.substring(0, comma).trim();
            }
            return ip;
        }
        ip = request.getHeader("X-Real-IP");
        if (StringUtils.hasText(ip) && !"unknown".equalsIgnoreCase(ip)) {
            return ip.trim();
        }
        ip = request.getHeader("Proxy-Client-IP");
        if (StringUtils.hasText(ip) && !"unknown".equalsIgnoreCase(ip)) {
            return ip.trim();
        }
        ip = request.getHeader("WL-Proxy-Client-IP");
        if (StringUtils.hasText(ip) && !"unknown".equalsIgnoreCase(ip)) {
            return ip.trim();
        }
        return request.getRemoteAddr();
    }

    /**
     * 获取客户端 User-Agent（浏览器/设备信息）
     *
     * @return User-Agent 字符串；获取不到返回 null
     */
    public static String getUserAgent() {
        HttpServletRequest request = getRequest();
        if (request == null) {
            return null;
        }
        String ua = request.getHeader("User-Agent");
        return StringUtils.hasText(ua) ? ua : null;
    }
}
