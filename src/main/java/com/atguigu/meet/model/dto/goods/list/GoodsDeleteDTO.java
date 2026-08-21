package com.atguigu.meet.model.dto.goods.list;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

/**
 * 商品批量删除 DTO
 */
@Data
public class GoodsDeleteDTO {

    @NotEmpty(message = "商品ids不能为空")
    private Long[] ids;
}
