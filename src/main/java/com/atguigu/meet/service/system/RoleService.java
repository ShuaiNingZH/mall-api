package com.atguigu.meet.service.system;

import com.atguigu.meet.common.Response;
import com.atguigu.meet.model.dto.system.RoleAssignMenuDTO;
import com.atguigu.meet.model.dto.system.RolePageQueryDTO;
import com.atguigu.meet.model.dto.system.RoleSaveDTO;
import com.atguigu.meet.model.dto.system.RoleUpdateDTO;

/**
 * 角色管理 Service
 */
public interface RoleService {

    /** 角色分页列表 */
    Response getPageList(RolePageQueryDTO parameter);

    /** 所有启用角色（下拉框用） */
    Response getAllRoles();

    /** 根据ID查角色（含已分配菜单ID列表） */
    Response getRoleById(Long id);

    /** 新增角色 */
    Response addRole(RoleSaveDTO dto);

    /** 修改角色 */
    Response updateRole(RoleUpdateDTO dto);

    /** 删除角色 */
    Response deleteRole(Long id);

    /** 查询角色已分配的菜单ID列表 */
    Response getRoleMenuIds(Long roleId);

    /** 给角色分配菜单（全量覆盖） */
    Response assignMenus(RoleAssignMenuDTO dto);
}
