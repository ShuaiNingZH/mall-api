package com.atguigu.meet.model.dto.permission.role;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 角色新增DTO
 */
@Data
public class RoleSaveDTO {
    @NotBlank(message = "角色名称不能为空")
    private String roleName;

    @NotBlank(message = "角色编码不能为空")
    private String roleCode;

    /** 状态 1启用 0禁用 */
    private Integer status;
}