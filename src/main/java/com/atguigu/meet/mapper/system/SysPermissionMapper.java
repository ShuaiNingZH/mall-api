package com.atguigu.meet.mapper.system;

import com.atguigu.meet.model.entity.system.SysPermission;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 权限 Mapper
 */
public interface SysPermissionMapper extends BaseMapper<SysPermission> {

    /**
     * 根据 userId 查询其拥有的全部权限码
     * 关联：sys_user_role -> sys_role_permission -> sys_permission
     */
    List<String> selectPermissionCodesByUserId(@Param("userId") Long userId);

    /**
     * 根据 userId 查询其拥有的全部角色编码
     */
    List<String> selectRoleCodesByUserId(@Param("userId") Long userId);
}
