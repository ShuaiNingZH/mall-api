package com.atguigu.meet.mapper.system;

import com.atguigu.meet.model.entity.system.SysUserRole;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户-角色关联 Mapper
 */
public interface SysUserRoleMapper extends BaseMapper<SysUserRole> {

    /**
     * 根据用户ID查询其角色ID列表
     */
    List<Long> selectRoleIdsByUserId(@Param("userId") Long userId);

    /**
     * 根据用户ID删除所有角色关联
     */
    int deleteByUserId(@Param("userId") Long userId);
}
