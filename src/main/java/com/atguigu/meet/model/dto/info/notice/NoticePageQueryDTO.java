package com.atguigu.meet.model.dto.info.notice;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 公告分页查询DTO
 */
@Data
public class NoticePageQueryDTO {
    @NotNull(message = "分页页码不能为空")
    private Integer pageNum;
    @NotNull(message = "每页条数不能为空")
    private Integer pageSize;

    /** 公告标题（模糊查询） */
    private String title;
    /** 状态 1启用 0禁用 */
    private Integer status;
    /** 创建开始时间（含） */
    private LocalDateTime startTime;
    /** 创建结束时间（含） */
    private LocalDateTime endTime;
}