package com.atguigu.meet.model.dto.permission.user;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 用户启用/禁用DTO
 */
@Data
public class UserStatusDTO {
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /** 目标状态 true启用 false禁用 */
    @NotNull(message = "目标状态不能为空")
    private Boolean status;
}
