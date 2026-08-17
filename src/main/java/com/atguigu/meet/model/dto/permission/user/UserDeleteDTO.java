package com.atguigu.meet.model.dto.permission.user;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

/**
 * @Description
 * @Date 2026-08-13 16:57
 */
@Data
public class UserDeleteDTO {
    @NotEmpty(message = "用户ids不能为空")
    private Long[] userIds;
}