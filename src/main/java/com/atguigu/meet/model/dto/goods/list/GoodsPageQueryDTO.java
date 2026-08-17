package com.atguigu.meet.model.dto.goods.list;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 商品分页查询DTO
 */
@Data
public class GoodsPageQueryDTO {
    @NotNull(message = "分页页码不能为空")
    private Integer pageNum;
    @NotNull(message = "每页条数不能为空")
    private Integer pageSize;

    /** 商品名称（模糊查询） */
    private String goodsName;
    /** 商品货号（精确查询） */
    private String goodsSn;
    /** 商品状态 0=下架 1=已上架 */
    private Integer status;
    /** 创建开始时间（含） */
    private LocalDateTime startTime;
    /** 创建结束时间（含） */
    private LocalDateTime endTime;
}
