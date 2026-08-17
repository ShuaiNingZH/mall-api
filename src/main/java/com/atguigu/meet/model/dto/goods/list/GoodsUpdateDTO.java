package com.atguigu.meet.model.dto.goods.list;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 商品修改DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GoodsUpdateDTO extends GoodsSaveDTO {
    @NotNull(message = "商品ID不能为空")
    private Long id;
}
