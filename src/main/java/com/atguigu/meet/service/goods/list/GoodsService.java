package com.atguigu.meet.service.goods.list;

import com.atguigu.meet.common.Response;
import com.atguigu.meet.model.dto.goods.list.GoodsDeleteDTO;
import com.atguigu.meet.model.dto.goods.list.GoodsPageQueryDTO;
import com.atguigu.meet.model.dto.goods.list.GoodsSaveDTO;
import com.atguigu.meet.model.dto.goods.list.GoodsStatusDTO;
import com.atguigu.meet.model.dto.goods.list.GoodsUpdateDTO;

/**
 * 商品管理 Service（商品列表模块）
 */
public interface GoodsService {

    /** 商品分页列表 */
    Response getPageList(GoodsPageQueryDTO parameter);

    /** 根据ID查商品 */
    Response getGoodsById(Long id);

    /** 新增商品 */
    Response addGoods(GoodsSaveDTO dto);

    /** 修改商品 */
    Response updateGoods(GoodsUpdateDTO dto);

    /** 商品上下架 */
    Response updateStatus(GoodsStatusDTO dto);

    /** 删除商品（逻辑删除） */
    Response deleteGoods(Long id);

    /** 批量删除商品（逻辑删除） */
    Response deleteGoodsBatch(GoodsDeleteDTO dto);
}
