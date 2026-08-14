package com.atguigu.meet.model.dto.system;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 用户分配角色DTO
 */
@Data
public class UserAssignRoleDTO {
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /** 角色ID列表（全量覆盖：传入的列表即为该用户最终拥有的角色） */
    private List<Long> roleIds;
}
