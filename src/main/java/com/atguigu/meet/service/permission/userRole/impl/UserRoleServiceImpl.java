package com.atguigu.meet.service.permission.userRole.impl;

import com.atguigu.meet.common.Response;
import com.atguigu.meet.mapper.permission.userRole.SysUserRoleMapper;
import com.atguigu.meet.model.dto.permission.userRole.UserAssignRoleDTO;
import com.atguigu.meet.model.entity.permission.userRole.SysUserRole;
import com.atguigu.meet.service.auth.PermissionCacheService;
import com.atguigu.meet.service.permission.userRole.UserRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户-角色关联 Service 实现
 */
@Service
@Slf4j
public class UserRoleServiceImpl extends ServiceImpl<SysUserRoleMapper, SysUserRole> implements UserRoleService {

    @Autowired
    private PermissionCacheService permissionCacheService;

    @Override
    public Response getUserRoleIds(Long userId) {
        List<Long> roleIds = baseMapper.selectRoleIdsByUserId(userId);
        return Response.ok(roleIds);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response assignRoles(UserAssignRoleDTO dto) {
        // 先删除旧关联
        baseMapper.deleteByUserId(dto.getUserId());

        // 再批量插入新关联
        if (dto.getRoleIds() != null && !dto.getRoleIds().isEmpty()) {
            List<SysUserRole> userRoles = dto.getRoleIds().stream().map(roleId -> {
                SysUserRole ur = new SysUserRole();
                ur.setUserId(dto.getUserId());
                ur.setRoleId(roleId);
                return ur;
            }).collect(Collectors.toList());

            for (SysUserRole ur : userRoles) {
                baseMapper.insert(ur);
            }
        }

        // 清除该用户的权限缓存
        permissionCacheService.invalidateUserPermissions(dto.getUserId());

        log.info("[用户角色] 分配角色成功，userId={}, roleIds={}", dto.getUserId(), dto.getRoleIds());
        return Response.ok("分配角色成功", null);
    }
}