package com.atguigu.meet.model.vo.permission.menu;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 菜单响应VO（支持树形结构）
 */
@Data
public class MenuVO {
    private Long id;
    private Long parentId;
    private String name;
    private String menuCode;
    private String perm;
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
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    /** 子菜单/按钮(树形结构) */
    private List<MenuVO> children;
}