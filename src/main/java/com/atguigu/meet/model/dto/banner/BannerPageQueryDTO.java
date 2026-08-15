package com.atguigu.meet.model.dto.banner;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 轮播图分页查询DTO
 */
@Data
public class BannerPageQueryDTO {
    @NotNull(message = "分页页码不能为空")
    private Integer pageNum;
    @NotNull(message = "每页条数不能为空")
    private Integer pageSize;

    /** 轮播位置：home=首页 seckill=抢购 */
    private String position;
    /** 状态 1启用 0禁用 */
    private Integer status;
    /** 创建开始时间（含） */
    private LocalDateTime startTime;
    /** 创建结束时间（含） */
    private LocalDateTime endTime;
}
