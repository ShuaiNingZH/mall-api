package com.atguigu.meet.model.dto.permission.menu;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 菜单修改DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MenuUpdateDTO extends MenuSaveDTO {
    @NotNull(message = "菜单ID不能为空")
    private Long id;
}