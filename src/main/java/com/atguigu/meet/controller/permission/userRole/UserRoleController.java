package com.atguigu.meet.controller.permission.userRole;

import com.atguigu.meet.common.Response;
import com.atguigu.meet.model.dto.permission.userRole.UserAssignRoleDTO;
import com.atguigu.meet.service.permission.userRole.UserRoleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 用户-角色关联接口
 */
@RestController
@RequestMapping("/user-roles")
@Validated
public class UserRoleController {
    @Autowired
    private UserRoleService userRoleService;

    /** 查询用户已分配的角色ID列表 */
    @GetMapping("/{userId}/roles")
    public Response getUserRoleIds(@PathVariable Long userId) {
        return userRoleService.getUserRoleIds(userId);
    }

    /** 给用户分配角色（全量覆盖） */
    @PutMapping("/roles")
    public Response assignRoles(@RequestBody @Valid UserAssignRoleDTO dto) {
        return userRoleService.assignRoles(dto);
    }
}