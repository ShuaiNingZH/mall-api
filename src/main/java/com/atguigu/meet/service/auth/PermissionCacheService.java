package com.atguigu.meet.service.auth;

import java.util.Set;

/**
 * 权限缓存服务接口
 */
public interface PermissionCacheService {

    /**
     * 获取用户权限集合（先查Redis，没有则查DB并缓存）
     *
     * @param userId 用户ID
     * @return 权限标识集合
     */
    Set<String> getUserPermissions(Long userId);

    /**
     * 从数据库加载用户权限并写入缓存
     *
     * @param userId 用户ID
     * @return 权限标识集合
     */
    Set<String> loadAndCachePermissions(Long userId);

    /**
     * 清除指定用户的权限缓存
     *
     * @param userId 用户ID
     */
    void invalidateUserPermissions(Long userId);

    /**
     * 清除所有用户的权限缓存
     */
    void invalidateAllPermissions();
}
