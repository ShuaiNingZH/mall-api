package com.atguigu.meet.model.dto.auth;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * @Description
 * @Date 2026-05-18 10:29
 */
@Data
public class AuthLoginDTO {
    @NotNull(message = "账号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String account;

    @NotNull(message = "密码不能为空")
    @Length(min = 6, max = 20, message = "密码长度 6-20 位")
    private String password;
}
