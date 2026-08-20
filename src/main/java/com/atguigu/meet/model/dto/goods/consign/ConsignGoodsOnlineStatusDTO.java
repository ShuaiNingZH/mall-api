package com.atguigu.meet.model.dto.goods.consign;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 抢购托售商品上下架DTO
 */
@Data
public class ConsignGoodsOnlineStatusDTO {
    @NotNull(message = "商品ID不能为空")
    private Long id;

    /** 目标上下架状态 false下架 true上架 */
    @NotNull(message = "目标状态不能为空")
    private Boolean onlineStatus;
}
