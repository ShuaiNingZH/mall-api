package com.atguigu.meet.model.dto.goods.list;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 商品上下架DTO
 */
@Data
public class GoodsStatusDTO {
    @NotNull(message = "商品ID不能为空")
    private Long id;

    /** 目标状态 0=下架 1=已上架 */
    @NotNull(message = "目标状态不能为空")
    @Min(value = 0, message = "目标状态只能为 0 或 1")
    @Max(value = 1, message = "目标状态只能为 0 或 1")
    private Integer status;
}
