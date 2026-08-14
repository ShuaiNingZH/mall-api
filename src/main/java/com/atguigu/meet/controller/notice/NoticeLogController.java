package com.atguigu.meet.controller.notice;

import com.atguigu.meet.annotation.RequirePermission;
import com.atguigu.meet.common.Response;
import com.atguigu.meet.constant.PermissionConst;
import com.atguigu.meet.model.dto.notice.NoticeLogPageQueryDTO;
import com.atguigu.meet.service.notice.NoticeLogService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 公告阅读日志接口（管理端：阅读记录与统计）
 */
@RestController
@RequestMapping("/notice-logs")
@Validated
public class NoticeLogController {
    @Autowired
    private NoticeLogService noticeLogService;

    /** 阅读日志分页列表（可按公告/用户筛选） */
    @GetMapping
    @RequirePermission(PermissionConst.NOTICE_LOG_QUERY)
    public Response getPageList(@Valid NoticeLogPageQueryDTO parameter) {
        return noticeLogService.getPageList(parameter);
    }

    /** 根据公告ID查询读者阅读记录列表 */
    @GetMapping("/by-notice/{noticeId}")
    @RequirePermission(PermissionConst.NOTICE_LOG_QUERY)
    public Response getReadersByNoticeId(@PathVariable Long noticeId) {
        return noticeLogService.getReadersByNoticeId(noticeId);
    }

    /** 根据公告ID查询阅读次数 */
    @GetMapping("/count/{noticeId}")
    @RequirePermission(PermissionConst.NOTICE_LOG_QUERY)
    public Response getReadCount(@PathVariable Long noticeId) {
        return noticeLogService.getReadCount(noticeId);
    }
}
