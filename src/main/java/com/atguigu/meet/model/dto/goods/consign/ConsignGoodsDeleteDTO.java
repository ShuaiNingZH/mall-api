package com.atguigu.meet.model.dto.goods.consign;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

/**
 * 抢购托售商品批量删除 DTO
 */
@Data
public class ConsignGoodsDeleteDTO {

    @NotEmpty(message = "托售商品ids不能为空")
    private Long[] ids;
}
