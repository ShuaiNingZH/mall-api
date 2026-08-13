package com.atguigu.meet.model.dto.user;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @Description
 * @Date 2026-05-09 16:46
 */
@Data
public class UserPageQueryDTO {
    // 分页参数
    @NotNull(message = "分页页码不能为空")
    private Integer pageNum;
    @NotNull(message = "每页条数不能为空")
    private Integer pageSize;

    // 业务查询条件
    private String username;
    private Integer age;
    private String phone;
}
