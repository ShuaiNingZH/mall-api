package com.atguigu.meet.service.invite;

import com.atguigu.meet.common.Response;
import com.atguigu.meet.model.entity.system.SysInviteCode;
import com.atguigu.meet.model.entity.system.SysInviteRecord;

import java.util.List;

/**
 * 邀请码服务接口
 */
public interface InviteCodeService {

    /**
     * 生成邀请码（1个用户只能生成1个，已存在则直接返回）
     */
    Response generateInviteCode(Long userId);

    /**
     * 查询我的邀请码
     */
    Response getMyInviteCode(Long userId);

    /**
     * 查询邀请明细流水
     */
    Response getInviteRecords(Long inviterId);

    /**
     * 校验邀请码有效性（供注册时调用）
     * 返回邀请码实体，无效则抛 BusinessException
     */
    SysInviteCode validateInviteCode(String inviteCode);

    /**
     * 注册成功后处理邀请流水（供注册时调用）
     */
    void processInviteRecord(SysInviteCode inviteCode, Long inviteeId, String inviteePhone);
}
