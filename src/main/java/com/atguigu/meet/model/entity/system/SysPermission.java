package com.atguigu.meet.model.entity.system;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统权限（权限码粒度，如 user:delete）
 */
@Data
@TableName("sys_permission")
public class SysPermission extends Model<SysPermission> {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long parentId;

    private String permissionName;

    private String permissionCode;

    private Integer type = 1;

    private Integer status = 1;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted = 0;
}
