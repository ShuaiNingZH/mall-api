package com.atguigu.meet.model.entity.system;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 系统角色（权限分组载体：管理员、普通用户、运营等）
 */
@Data
@TableName("sys_role")
public class SysRole extends Model<SysRole> {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String roleName;

    private String roleCode;

    private Integer status = 1;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    @TableLogic
    private Integer isDeleted = 0;

    /** 角色拥有的菜单/权限（非数据库字段） */
    @TableField(exist = false)
    private List<SysMenu> menus;
}
