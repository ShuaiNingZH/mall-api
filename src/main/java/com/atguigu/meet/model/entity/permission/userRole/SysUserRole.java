package com.atguigu.meet.model.entity.permission.userRole;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

/**
 * 用户-角色关联
 */
@Data
@TableName("sys_user_role")
public class SysUserRole extends Model<SysUserRole> {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long roleId;
}