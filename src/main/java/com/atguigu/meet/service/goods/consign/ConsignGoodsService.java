package com.atguigu.meet.service.goods.consign;

import com.atguigu.meet.common.Response;
import com.atguigu.meet.model.dto.goods.consign.ConsignGoodsBizStatusDTO;
import com.atguigu.meet.model.dto.goods.consign.ConsignGoodsOnlineStatusDTO;
import com.atguigu.meet.model.dto.goods.consign.ConsignGoodsPageQueryDTO;
import com.atguigu.meet.model.dto.goods.consign.ConsignGoodsSaveDTO;
import com.atguigu.meet.model.dto.goods.consign.ConsignGoodsUpdateDTO;

/**
 * 抢购托售商品 Service
 * <p>
 * 业务状态流转：1挂卖中 -> 2已抢购待付款 -> 3等待确认付款 -> 4待处理 -> 5委托代卖 -> 6委托发货
 */
public interface ConsignGoodsService {

    /** 分页列表（含委托人信息 + 场次名称） */
    Response getPageList(ConsignGoodsPageQueryDTO parameter);

    /** 根据ID查详情（含委托人信息 + 场次名称） */
    Response getConsignGoodsById(Long id);

    /** 新增 */
    Response addConsignGoods(ConsignGoodsSaveDTO dto);

    /** 修改 */
    Response updateConsignGoods(ConsignGoodsUpdateDTO dto);

    /** 上下架 */
    Response updateOnlineStatus(ConsignGoodsOnlineStatusDTO dto);

    /** 业务状态流转 */
    Response updateBizStatus(ConsignGoodsBizStatusDTO dto);

    /** 删除（逻辑删除） */
    Response deleteConsignGoods(Long id);
}
