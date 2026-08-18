package com.atguigu.meet.model.dto.permission.menu;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 菜单新增DTO
 */
@Data
public class MenuSaveDTO {
    @NotNull(message = "父菜单ID不能为空")
    private Long parentId;

    @NotBlank(message = "菜单名称不能为空")
    private String name;

    /** 菜单编码(目录/菜单可用，如 sys) */
    private String menuCode;

    /** 权限标识(按钮用，格式: 模块:页面:操作，如 sys:user:delete) */
    private String perm;

    @NotNull(message = "菜单类型不能为空")
    private Integer type;

    private String path;
    private String routeName;
    private String componentPath;
    private String icon;
    private Integer sort;
    private Integer visible;
    private Integer keepAlive;
    private String activeMenu;
    private Integer hideInMenu;
    private Integer hideInTag;
    private Integer hideParent;
    private Integer status;
}