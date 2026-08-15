package com.atguigu.meet.model.dto.notice;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 公告阅读日志分页查询DTO
 */
@Data
public class NoticeLogPageQueryDTO {
    @NotNull(message = "分页页码不能为空")
    private Integer pageNum;
    @NotNull(message = "每页条数不能为空")
    private Integer pageSize;

    /** 公告ID */
    private Long noticeId;
    /** 用户ID(sys_user.id) */
    private Long userId;
    /** 创建开始时间（含） */
    private LocalDateTime startTime;
    /** 创建结束时间（含） */
    private LocalDateTime endTime;
}
