package com.atguigu.meet.model.dto.goods.consign;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 抢购托售商品修改DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ConsignGoodsUpdateDTO extends ConsignGoodsSaveDTO {
    @NotNull(message = "商品ID不能为空")
    private Long id;
}
