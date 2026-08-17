package com.atguigu.meet.model.entity.permission.menu;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 系统菜单/权限(目录-菜单-按钮统一管理)
 * 通过 type 区分：0目录 1菜单 2按钮权限
 * 通过 parent_id 自关联形成目录-菜单-按钮三级树
 */
@Data
@TableName("sys_menu")
public class SysMenu extends Model<SysMenu> {
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 父菜单ID(0表示顶级) */
    private Long parentId;

    private String menuName;

    /** 菜单编码(目录/菜单可用，如 sys) */
    private String menuCode;

    /** 权限标识(按钮用，如 user:delete) */
    private String perms;

    /** 类型 0目录 1菜单 2按钮权限 */
    private Integer type = 1;

    /** 路由路径(目录/菜单) */
    private String path;

    /** 路由名称(前端keep-alive匹配用) */
    private String routeName;

    /** 前端组件路径(菜单) */
    private String component;

    private String icon;

    /** 排序(数字越小越靠前) */
    private Integer sort = 0;

    /** 是否可见 1可见 0隐藏 */
    private Integer visible = 1;

    /** 是否缓存组件 1是 0否 */
    private Integer keepAlive = 0;

    /** 高亮菜单path(详情页等场景) */
    private String activeMenu;

    /** 是否在标签栏隐藏 1是 0否 */
    private Integer hideInTag = 0;

    /** 是否隐藏父级菜单 1是 0否 */
    private Integer hideParent = 0;

    /** 状态 1启用 0禁用 */
    private Integer status = 1;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    @JsonIgnore
    @TableLogic
    private Integer isDeleted = 0;

    /** 子菜单/按钮(树形结构，非数据库字段) */
    @TableField(exist = false)
    private List<SysMenu> children;
}