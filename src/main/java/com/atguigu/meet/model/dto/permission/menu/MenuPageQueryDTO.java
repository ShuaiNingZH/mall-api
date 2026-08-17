package com.atguigu.meet.model.dto.permission.menu;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 菜单分页查询DTO
 */
@Data
public class MenuPageQueryDTO {
    @NotNull(message = "分页页码不能为空")
    private Integer pageNum;
    @NotNull(message = "每页条数不能为空")
    private Integer pageSize;

    /** 菜单名称（模糊查询） */
    private String menuName;
    /** 类型 0目录 1菜单 2按钮权限 */
    private Integer type;
    /** 状态 1启用 0禁用 */
    private Integer status;
}