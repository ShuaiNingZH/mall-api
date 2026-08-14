package com.atguigu.meet.service.auth.impl;

import com.atguigu.meet.config.PermissionCacheProperties;
import com.atguigu.meet.mapper.system.SysMenuMapper;
import com.atguigu.meet.service.auth.PermissionCacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 权限缓存服务实现类
 * <p>
 * 权限获取流程：
 * 1. 先查Redis缓存
 * 2. Redis没有则执行多表联查数据库（sys_user_role -> sys_role_menu -> sys_menu）
 * 3. 将结果写入Redis并设置过期时间
 */
@Service
@Slf4j
public class PermissionCacheServiceImpl implements PermissionCacheService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private PermissionCacheProperties permissionCacheProperties;

    @Autowired
    private SysMenuMapper sysMenuMapper;

    /**
     * 构建用户权限缓存的 Redis Key
     */
    private String buildCacheKey(Long userId) {
        return permissionCacheProperties.getCachePrefix() + userId;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Set<String> getUserPermissions(Long userId) {
        if (userId == null) {
            return Collections.emptySet();
        }

        String cacheKey = buildCacheKey(userId);

        // 1. 先查 Redis 缓存
        Object cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            if (cached instanceof List) {
                List<String> list = (List<String>) cached;
                log.debug("[权限缓存] Redis命中，userId={}, 权限数={}", userId, list.size());
                return new HashSet<>(list);
            }
        }

        // 2. Redis 没有，从 DB 加载并写入缓存
        log.debug("[权限缓存] Redis未命中，userId={}，从数据库加载", userId);
        return loadAndCachePermissions(userId);
    }

    @Override
    public Set<String> loadAndCachePermissions(Long userId) {
        if (userId == null) {
            return Collections.emptySet();
        }

        // 多表联查：sys_user_role -> sys_role -> sys_role_menu -> sys_menu
        List<String> permsList = sysMenuMapper.selectPermsByUserId(userId);
        Set<String> permsSet = permsList.stream()
                .filter(Objects::nonNull)
                .filter(p -> !p.trim().isEmpty())
                .collect(Collectors.toSet());

        // 写入 Redis，设置过期时间
        String cacheKey = buildCacheKey(userId);
        long expireSeconds = permissionCacheProperties.getExpireSeconds();
        redisTemplate.opsForValue().set(
                cacheKey,
                new ArrayList<>(permsSet),
                expireSeconds,
                TimeUnit.SECONDS
        );
        log.info("[权限缓存] 已写入Redis，userId={}, 权限数={}, 过期时间={}s",
                userId, permsSet.size(), expireSeconds);

        return permsSet;
    }

    @Override
    public void invalidateUserPermissions(Long userId) {
        if (userId == null) {
            return;
        }
        String cacheKey = buildCacheKey(userId);
        Boolean deleted = redisTemplate.delete(cacheKey);
        log.info("[权限缓存] 清除用户权限缓存，userId={}, 结果={}", userId, deleted);
    }

    @Override
    public void invalidateAllPermissions() {
        String pattern = permissionCacheProperties.getCachePrefix() + "*";
        Set<String> keys = redisTemplate.keys(pattern);
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
            log.info("[权限缓存] 批量清除所有权限缓存，数量={}", keys.size());
        }
    }
}
