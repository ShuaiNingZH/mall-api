package com.atguigu.meet.service.goods.consign.impl;

import com.atguigu.meet.common.Response;
import com.atguigu.meet.mapper.goods.consign.ConsignGoodsMapper;
import com.atguigu.meet.mapper.permission.user.UserMapper;
import com.atguigu.meet.model.dto.goods.consign.ConsignGoodsBizStatusDTO;
import com.atguigu.meet.model.dto.goods.consign.ConsignGoodsOnlineStatusDTO;
import com.atguigu.meet.model.dto.goods.consign.ConsignGoodsPageQueryDTO;
import com.atguigu.meet.model.dto.goods.consign.ConsignGoodsSaveDTO;
import com.atguigu.meet.model.dto.goods.consign.ConsignGoodsUpdateDTO;
import com.atguigu.meet.model.entity.goods.consign.ConsignGoods;
import com.atguigu.meet.model.entity.permission.user.SysUser;
import com.atguigu.meet.model.vo.PageResultVO;
import com.atguigu.meet.model.vo.goods.consign.ConsignGoodsVO;
import com.atguigu.meet.service.goods.consign.ConsignGoodsService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import com.atguigu.meet.utils.BeanConvertUtils;
import com.atguigu.meet.utils.TimeRangeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 抢购托售商品 Service 实现
 * <p>
 * 核心保障：
 * - 委托人信息实时同步：列表/详情通过 JOIN sys_user 获取最新委托人数据，用户表更新后自动同步
 * - 业务状态流转校验：仅允许合法的前后状态迁移
 * - 上下架独立控制：online_status 与 goods_status 分离
 * - 软删除：@TableLogic 逻辑删除
 * - 入参强校验：DTO @Valid + XSS 防护
 */
@Service
@Slf4j
public class ConsignGoodsServiceImpl extends ServiceImpl<ConsignGoodsMapper, ConsignGoods> implements ConsignGoodsService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public Response getPageList(ConsignGoodsPageQueryDTO parameter) {
        // 解析时间范围：timeRange[0] -> 当天 00:00:00，timeRange[1] -> 当天 23:59:59
        LocalDateTime startTime = null;
        LocalDateTime endTime = null;
        List<String> timeRange = parameter.getTimeRange();
        if (timeRange != null && !timeRange.isEmpty()) {
            if (timeRange.size() >= 1) {
                startTime = TimeRangeUtils.toStartOfDay(timeRange.get(0));
            }
            if (timeRange.size() >= 2) {
                endTime = TimeRangeUtils.toEndOfDay(timeRange.get(1));
            }
        }

        Page<ConsignGoodsVO> page = new Page<>(parameter.getPageNum(), parameter.getPageSize());
        IPage<ConsignGoodsVO> result = baseMapper.selectConsignGoodsPage(
                page,
                parameter.getGoodsName(),
                parameter.getMemberId(),
                parameter.getSessionId(),
                parameter.getGoodsStatus(),
                parameter.getOnlineStatus(),
                startTime,
                endTime
        );
        return Response.ok(PageResultVO.of(result));
    }

    @Override
    public Response getConsignGoodsById(Long id) {
        ConsignGoodsVO vo = baseMapper.selectConsignGoodsById(id);
        if (vo == null) {
            return Response.fail(500, "商品不存在");
        }
        return Response.ok(vo);
    }

    @Override
    public Response addConsignGoods(ConsignGoodsSaveDTO dto) {
        // 校验委托人是否存在
        SysUser member = userMapper.selectById(dto.getMemberId());
        if (member == null) {
            return Response.fail(500, "委托人不存在");
        }
        ConsignGoods goods = new ConsignGoods();
        BeanConvertUtils.copyProperties(dto, goods);
        // 兜底 XSS 防护：转义字符串字段
        goods.setGoodsName(escape(goods.getGoodsName()));
        goods.setCoverImg(escape(goods.getCoverImg()));
        goods.setDetailImg(escape(goods.getDetailImg()));
        // 默认值：业务状态不传则 1 挂卖中，上下架不传则 0 下架
        if (goods.getGoodsStatus() == null) {
            goods.setGoodsStatus(1);
        }
        if (goods.getOnlineStatus() == null) {
            goods.setOnlineStatus(0);
        }
        goods.setSaleTimes(0);
        save(goods);
        log.info("[托售商品] 新增成功，id={}, goodsName={}, memberId={}",
                goods.getId(), goods.getGoodsName(), goods.getMemberId());
        return Response.ok("新增成功", null);
    }

    @Override
    public Response updateConsignGoods(ConsignGoodsUpdateDTO dto) {
        ConsignGoods existGoods = getById(dto.getId());
        if (existGoods == null) {
            return Response.fail(500, "商品不存在");
        }
        // 校验委托人是否存在
        SysUser member = userMapper.selectById(dto.getMemberId());
        if (member == null) {
            return Response.fail(500, "委托人不存在");
        }
        ConsignGoods goods = new ConsignGoods();
        BeanConvertUtils.copyProperties(dto, goods);
        // 兜底 XSS 防护：转义字符串字段
        goods.setGoodsName(escape(goods.getGoodsName()));
        goods.setCoverImg(escape(goods.getCoverImg()));
        goods.setDetailImg(escape(goods.getDetailImg()));
        updateById(goods);
        log.info("[托售商品] 修改成功，id={}", dto.getId());
        return Response.ok("修改成功", null);
    }

    @Override
    public Response updateOnlineStatus(ConsignGoodsOnlineStatusDTO dto) {
        ConsignGoods existGoods = getById(dto.getId());
        if (existGoods == null) {
            return Response.fail(500, "商品不存在");
        }
        ConsignGoods goods = new ConsignGoods();
        goods.setId(dto.getId());
        goods.setOnlineStatus(Boolean.TRUE.equals(dto.getOnlineStatus()) ? 1 : 0);
        updateById(goods);
        log.info("[托售商品] 上下架成功，id={}, {}->{}",
                dto.getId(), existGoods.getOnlineStatus(), dto.getOnlineStatus());
        return Response.ok("上下架成功", null);
    }

    @Override
    public Response updateBizStatus(ConsignGoodsBizStatusDTO dto) {
        ConsignGoods existGoods = getById(dto.getId());
        if (existGoods == null) {
            return Response.fail(500, "商品不存在");
        }
        Integer fromStatus = existGoods.getGoodsStatus();
        Integer toStatus = dto.getGoodsStatus();
        // 业务状态流转校验
        if (!isValidTransition(fromStatus, toStatus)) {
            return Response.fail(500, String.format("业务状态不允许从[%s]流转到[%s]",
                    statusName(fromStatus), statusName(toStatus)));
        }
        ConsignGoods goods = new ConsignGoods();
        goods.setId(dto.getId());
        goods.setGoodsStatus(toStatus);
        updateById(goods);
        log.info("[托售商品] 业务状态流转成功，id={}, {}->{}",
                dto.getId(), statusName(fromStatus), statusName(toStatus));
        return Response.ok("业务状态流转成功", null);
    }

    @Override
    public Response deleteConsignGoods(Long id) {
        ConsignGoods existGoods = getById(id);
        if (existGoods == null) {
            return Response.fail(500, "商品不存在");
        }
        removeById(id);
        log.info("[托售商品] 删除成功（逻辑删除），id={}", id);
        return Response.ok("删除成功", null);
    }

    // ====================== 私有方法 ======================

    /**
     * 业务状态流转合法性校验
     * <p>
     * 合法流转：
     * 1挂卖中 -> 2已抢购待付款 / 4待处理 / 5委托代卖
     * 2已抢购待付款 -> 3等待确认付款 / 4待处理
     * 3等待确认付款 -> 4待处理 / 5委托代卖
     * 4待处理 -> 5委托代卖 / 6委托发货 / 1挂卖中
     * 5委托代卖 -> 6委托发货 / 1挂卖中
     * 6委托发货 -> 1挂卖中（发货完成后重新挂卖）
     * 同状态不允许流转
     */
    private boolean isValidTransition(Integer from, Integer to) {
        if (from == null || to == null || from.equals(to)) {
            return false;
        }
        // 使用邻接表定义合法流转
        Map<Integer, int[]> transitions = new LinkedHashMap<>();
        transitions.put(1, new int[]{2, 4, 5});
        transitions.put(2, new int[]{3, 4});
        transitions.put(3, new int[]{4, 5});
        transitions.put(4, new int[]{1, 5, 6});
        transitions.put(5, new int[]{1, 6});
        transitions.put(6, new int[]{1});
        int[] allowed = transitions.get(from);
        if (allowed == null) {
            return false;
        }
        for (int s : allowed) {
            if (s == to) {
                return true;
            }
        }
        return false;
    }

    /** 业务状态中文名称 */
    private String statusName(Integer status) {
        if (status == null) return "未知";
        switch (status) {
            case 1: return "挂卖中";
            case 2: return "已抢购待付款";
            case 3: return "等待确认付款";
            case 4: return "待处理";
            case 5: return "委托代卖";
            case 6: return "委托发货";
            default: return "未知";
        }
    }

    /**
     * XSS 兜底防护：转义 HTML 特殊字符
     */
    private String escape(String input) {
        if (!StringUtils.hasText(input)) {
            return input;
        }
        return input.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
