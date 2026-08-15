package com.atguigu.meet.controller.invite;

import com.atguigu.meet.common.Response;
import com.atguigu.meet.service.invite.InviteCodeService;
import com.atguigu.meet.utils.AdminContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 邀请码相关接口
 */
@RestController
@RequestMapping("/invite")
public class InviteCodeController {

    @Autowired
    private InviteCodeService inviteCodeService;

    /**
     * 生成我的邀请码（1人1码，已存在则直接返回）
     */
    @PostMapping("/code/generate")
    public Response generateInviteCode() {
        Long userId = AdminContext.getLoginUserId();
        return inviteCodeService.generateInviteCode(userId);
    }

    /**
     * 查询我的邀请码
     */
    @GetMapping("/code/mine")
    public Response getMyInviteCode() {
        Long userId = AdminContext.getLoginUserId();
        return inviteCodeService.getMyInviteCode(userId);
    }

    /**
     * 查询我的邀请明细流水
     */
    @GetMapping("/records")
    public Response getInviteRecords() {
        Long userId = AdminContext.getLoginUserId();
        return inviteCodeService.getInviteRecords(userId);
    }
}
