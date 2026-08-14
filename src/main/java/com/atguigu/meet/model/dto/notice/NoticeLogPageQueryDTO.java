package com.atguigu.meet.model.dto.notice;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

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
}
