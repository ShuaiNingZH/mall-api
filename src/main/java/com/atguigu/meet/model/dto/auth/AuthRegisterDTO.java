package com.atguigu.meet.model.dto.auth;

import com.atguigu.meet.model.dto.user.UserBaseDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 用户新增请求DTO
 * 为什么不用重写 setter？
 * 赋值是 Spring 用 setter 完成的
 * 校验是校验框架读 getter 上的注解规则
 * 两者分工完全分开：
 * setter：接收前端参数赋值
 * getter：被校验框架读取规则、做校验
 */
@Data
public class AuthRegisterDTO extends UserBaseDTO {
    @NotBlank(message = "用户名不能为空")
    @Override
    public String getUsername() {
        return super.getUsername();
    }

    @NotBlank(message = "密码不能为空")
    @Override
    public String getPassword() {
        return super.getPassword();
    }

    @NotNull(message = "年龄不能为空")
    @Override
    public Integer getAge() {
        return super.getAge();
    }

    @NotNull(message = "手机号不能为空")
    @Override
    public String getPhone() {
        return super.getPhone();
    }
}