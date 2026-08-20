package com.atguigu.meet.mapper.goods.consign;

import com.atguigu.meet.model.entity.goods.consign.ConsignGoods;
import com.atguigu.meet.model.vo.goods.consign.ConsignGoodsVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

/**
 * 抢购托售商品 Mapper
 * <p>
 * 通过 JOIN sys_user 实时获取委托人信息，用户表数据更新后列表/详情自动同步。
 */
public interface ConsignGoodsMapper extends BaseMapper<ConsignGoods> {

    /**
     * 分页查询抢购托售商品（含委托人信息 + 场次名称）
     *
     * @param page      分页参数
     * @param goodsName 商品名称（模糊）
     * @param memberId  委托人ID
     * @param sessionId 场次ID
     * @param goodsStatus 业务状态
     * @param onlineStatus 上下架状态
     * @param startTime 创建开始时间
     * @param endTime   创建结束时间
     */
    IPage<ConsignGoodsVO> selectConsignGoodsPage(Page<ConsignGoodsVO> page,
                                                 @Param("goodsName") String goodsName,
                                                 @Param("memberId") Long memberId,
                                                 @Param("sessionId") Long sessionId,
                                                 @Param("goodsStatus") Integer goodsStatus,
                                                 @Param("onlineStatus") Integer onlineStatus,
                                                 @Param("startTime") Object startTime,
                                                 @Param("endTime") Object endTime);

    /**
     * 根据ID查询抢购托售商品详情（含委托人信息 + 场次名称）
     */
    ConsignGoodsVO selectConsignGoodsById(@Param("id") Long id);
}
