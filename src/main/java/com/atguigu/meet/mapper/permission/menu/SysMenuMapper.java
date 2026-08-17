package com.atguigu.meet.mapper.permission.menu;

import com.atguigu.meet.model.entity.permission.menu.SysMenu;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 菜单/权限 Mapper
 */
public interface SysMenuMapper extends BaseMapper<SysMenu> {

    /**
     * 根据 userId 查询其拥有的全部权限标识(perms)
     * 关联：sys_user_role -> sys_role_menu -> sys_menu
     */
    List<String> selectPermsByUserId(@Param("userId") Long userId);

    /**
     * 根据 userId 查询其拥有的全部角色编码
     */
    List<String> selectRoleCodesByUserId(@Param("userId") Long userId);
}