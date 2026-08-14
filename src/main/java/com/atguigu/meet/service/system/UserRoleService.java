package com.atguigu.meet.service.system;

import com.atguigu.meet.common.Response;
import com.atguigu.meet.model.dto.system.UserAssignRoleDTO;

/**
 * 用户-角色关联 Service
 */
public interface UserRoleService {

    /** 查询用户已分配的角色ID列表 */
    Response getUserRoleIds(Long userId);

    /** 给用户分配角色（全量覆盖） */
    Response assignRoles(UserAssignRoleDTO dto);
}
