package com.atguigu.meet.model.dto.permission.role;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色修改DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RoleUpdateDTO extends RoleSaveDTO {
    @NotNull(message = "角色ID不能为空")
    private Long id;
}