package com.atguigu.meet.model.entity.system;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

/**
 * 角色-权限关联
 */
@Data
@TableName("sys_role_permission")
public class SysRolePermission extends Model<SysRolePermission> {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long roleId;

    private Long permissionId;
}
