package com.atguigu.meet.model.dto.notice;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

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
}
