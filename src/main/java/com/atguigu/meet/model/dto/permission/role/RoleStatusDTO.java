package com.atguigu.meet.model.dto.permission.role;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 角色启用/禁用DTO
 */
@Data
public class RoleStatusDTO {
    @NotNull(message = "角色ID不能为空")
    private Long id;

    /** 目标状态 true启用 false禁用 */
    @NotNull(message = "目标状态不能为空")
    private Boolean status;
}
