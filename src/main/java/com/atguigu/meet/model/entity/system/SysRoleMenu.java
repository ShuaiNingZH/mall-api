package com.atguigu.meet.model.entity.system;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

/**
 * 角色-菜单关联(多对多：一个角色绑定多个菜单/按钮)
 */
@Data
@TableName("sys_role_menu")
public class SysRoleMenu extends Model<SysRoleMenu> {
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 角色ID(sys_role.id) */
    private Long roleId;

    /** 菜单/权限ID(sys_menu.id) */
    private Long menuId;
}
