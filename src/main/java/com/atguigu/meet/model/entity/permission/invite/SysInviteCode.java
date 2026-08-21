package com.atguigu.meet.model.entity.permission.invite;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 邀请码实体类
 * 1个用户只能生成1个邀请码（uk_inviter 唯一索引保证）
 * 单邀请码最多邀请10人注册
 */
@Data
@TableName("sys_invite_code")
public class SysInviteCode extends Model<SysInviteCode> {
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 原始序列号（Redis 发号器分配，与 invite_code 一一对应，便于 MAX(seq) 兜底恢复） */
    private Long seq;

    /** 邀请码（8位，区分大小写，数字+字母，由 seq 经 54 进制编码生成） */
    @Pattern(regexp = "^[A-Za-z0-9]{8}$", message = "邀请码格式不正确")
    private String inviteCode;

    /** 邀请人ID（生成者） */
    private Long inviterId;

    /** 0可用 1手动失效 2名额已满停用 */
    private Integer status = 0;

    /** 最大可邀请人数：10 */
    private Integer maxInviteNum = 10;

    /** 已邀请注册人数（冗余，仅展示） */
    private Integer usedInviteNum = 0;

    /** 过期时间，null永久有效 */
    private LocalDateTime expireTime;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    @JsonIgnore
    @TableLogic
    private Integer isDeleted = 0;
}
