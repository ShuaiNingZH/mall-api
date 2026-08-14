package com.atguigu.meet.service.notice;

import com.atguigu.meet.common.Response;
import com.atguigu.meet.model.dto.notice.NoticeLogPageQueryDTO;

/**
 * 公告阅读日志 Service
 */
public interface NoticeLogService {

    /** 阅读日志分页列表（管理端） */
    Response getPageList(NoticeLogPageQueryDTO parameter);

    /** 根据公告ID查询读者阅读记录列表 */
    Response getReadersByNoticeId(Long noticeId);

    /** 根据公告ID查询阅读次数 */
    Response getReadCount(Long noticeId);

    /**
     * 记录用户阅读公告行为<p>
     * 语义：同一用户同一公告只记录一次，重复阅读刷新阅读时间。<br>
     * 注意：调用方通常为用户端阅读接口（持有 sys_user 上下文），管理端默认不暴露此入口。
     *
     * @param noticeId 公告ID
     * @param userId   用户ID(sys_user.id)
     */
    Response recordRead(Long noticeId, Long userId);
}
