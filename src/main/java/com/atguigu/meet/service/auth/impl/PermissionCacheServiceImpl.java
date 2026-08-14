package com.atguigu.meet.service.auth.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
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
 * 1. 先查Redis缓存（String Key + JSON Value）
 * 2. Redis没有则执行多表联查数据库（sys_user_role -> sys_role_menu -> sys_menu）
 * 3. 将结果以JSON格式写入Redis并设置过期时间
 */
@Service
@Slf4j
public class PermissionCacheServiceImpl implements PermissionCacheService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

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
    public Set<String> getUserPermissions(Long userId) {
        if (userId == null) {
            return Collections.emptySet();
        }

        String cacheKey = buildCacheKey(userId);

        // 1. 先查 Redis 缓存
        String cachedJson = redisTemplate.opsForValue().get(cacheKey);
        if (cachedJson != null && !cachedJson.isEmpty()) {
            try {
                // 手动反序列化 JSON 数组为 List<String>
                JSONArray jsonArray = JSON.parseArray(cachedJson);
                Set<String> perms = new HashSet<>();
                for (int i = 0; i < jsonArray.size(); i++) {
                    String perm = jsonArray.getString(i);
                    if (perm != null && !perm.trim().isEmpty()) {
                        perms.add(perm);
                    }
                }
                log.info("[权限缓存] Redis命中，userId={}, 权限数={}", userId, perms.size());
                return perms;
            } catch (Exception e) {
                log.warn("[权限缓存] Redis反序列化失败，userId={}, error={}", userId, e.getMessage());
                // 反序列化失败，删除脏缓存，重新加载
                redisTemplate.delete(cacheKey);
            }
        }

        // 2. Redis 没有，从 DB 加载并写入缓存
        log.info("[权限缓存] Redis未命中，userId={}，从数据库加载", userId);
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

        // 写入 Redis，手动 JSON 序列化
        String cacheKey = buildCacheKey(userId);
        long expireSeconds = permissionCacheProperties.getExpireSeconds();
        String jsonValue = JSON.toJSONString(new ArrayList<>(permsSet));
        redisTemplate.opsForValue().set(cacheKey, jsonValue, expireSeconds, TimeUnit.SECONDS);

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
