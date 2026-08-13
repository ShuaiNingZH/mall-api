package com.atguigu.meet.model.dto.user;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 用户更新请求DTO
 */
@Data
public class UserUpdateDTO extends UserBaseDTO {
    @NotNull(message = "用户 id 不能为空")
    private Long id;
}