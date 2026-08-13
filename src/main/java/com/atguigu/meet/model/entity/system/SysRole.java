package com.atguigu.meet.model.entity.system;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统角色
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
    private Integer deleted = 0;
}
