package com.atguigu.meet.model.dto.permission.role;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 角色分配菜单DTO
 */
@Data
public class RoleAssignMenuDTO {
    @NotNull(message = "角色ID不能为空")
    private Long roleId;

    /** 菜单ID列表（全量覆盖：传入的列表即为该角色最终拥有的菜单权限） */
    private List<Long> menuIds;
}