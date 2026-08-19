package com.atguigu.meet.model.dto.goods.list;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 商品上下架DTO
 */
@Data
public class GoodsStatusDTO {
    @NotNull(message = "商品ID不能为空")
    private Long id;

    /** 目标状态 false=下架 true=已上架 */
    @NotNull(message = "目标状态不能为空")
    private Boolean status;
}
