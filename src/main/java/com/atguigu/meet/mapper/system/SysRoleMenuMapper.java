package com.atguigu.meet.mapper.system;

import com.atguigu.meet.model.entity.system.SysRoleMenu;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 角色-菜单关联 Mapper
 */
public interface SysRoleMenuMapper extends BaseMapper<SysRoleMenu> {

    /**
     * 根据角色ID查询其菜单ID列表
     */
    List<Long> selectMenuIdsByRoleId(@Param("roleId") Long roleId);

    /**
     * 根据角色ID删除所有菜单关联
     */
    int deleteByRoleId(@Param("roleId") Long roleId);
}
