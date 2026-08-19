package com.atguigu.meet.service.goods.list.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.meet.common.Response;
import com.atguigu.meet.mapper.goods.list.GoodsMapper;
import com.atguigu.meet.mapper.goods.list.GoodsOperateLogMapper;
import com.atguigu.meet.model.dto.goods.list.GoodsPageQueryDTO;
import com.atguigu.meet.model.dto.goods.list.GoodsSaveDTO;
import com.atguigu.meet.model.dto.goods.list.GoodsStatusDTO;
import com.atguigu.meet.model.dto.goods.list.GoodsUpdateDTO;
import com.atguigu.meet.model.entity.goods.list.Goods;
import com.atguigu.meet.model.entity.goods.list.GoodsOperateLog;
import com.atguigu.meet.model.vo.PageResultVO;
import com.atguigu.meet.service.goods.list.GoodsService;
import com.atguigu.meet.utils.AdminContext;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import com.atguigu.meet.utils.BeanConvertUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 商品管理 Service 实现（商品列表模块）
 * <p>
 * 核心保障：
 * - goods_sn 全局唯一：DB 唯一索引 + Service 层预校验
 * - price ≥ 0、stock ≥ 0：DTO 层 @DecimalMin / @Min 校验
 * - 软删除：@TableLogic 逻辑删除，查询默认过滤已删除
 * - 入参强校验：DTO @Valid + XSS 防护（@Pattern 拒绝 < > + 落库前 HtmlUtils.htmlEscape 兜底）
 * - 操作日志：新增/编辑/删除/上下架 均写入 t_goods_operate_log
 */
@Service
@Slf4j
public class GoodsServiceImpl extends ServiceImpl<GoodsMapper, Goods> implements GoodsService {

    @Autowired
    private GoodsOperateLogMapper goodsOperateLogMapper;

    @Override
    public Response getPageList(GoodsPageQueryDTO parameter) {
        LambdaQueryWrapper<Goods> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(parameter.getGoodsName())) {
            wrapper.like(Goods::getGoodsName, parameter.getGoodsName());
        }
        if (StringUtils.hasText(parameter.getGoodsSn())) {
            wrapper.eq(Goods::getGoodsSn, parameter.getGoodsSn());
        }
        if (parameter.getStatus() != null) {
            wrapper.eq(Goods::getStatus, parameter.getStatus());
        }
        if (parameter.getStartTime() != null) {
            wrapper.ge(Goods::getCreateTime, parameter.getStartTime());
        }
        if (parameter.getEndTime() != null) {
            wrapper.le(Goods::getCreateTime, parameter.getEndTime());
        }
        wrapper.orderByDesc(Goods::getCreateTime);

        IPage<Goods> page = new Page<>(parameter.getPageNum(), parameter.getPageSize());
        IPage<Goods> result = page(page, wrapper);
        return Response.ok(PageResultVO.of(result));
    }

    @Override
    public Response getGoodsById(Long id) {
        Goods goods = getById(id);
        if (goods == null) {
            return Response.fail(500, "商品不存在");
        }
        return Response.ok(goods);
    }

    @Override
    public Response addGoods(GoodsSaveDTO dto) {
        // 货号唯一性预校验（DB 唯一索引兜底，这里提前给出友好提示）
        if (existsByGoodsSn(dto.getGoodsSn(), null)) {
            return Response.fail(500, "商品货号已存在");
        }
        Goods goods = new Goods();
        BeanConvertUtils.copyProperties(dto, goods);
        // 兜底 XSS 防护：转义字符串字段
        goods.setGoodsName(escape(goods.getGoodsName()));
        goods.setGoodsSn(escape(goods.getGoodsSn()));
        goods.setGoodsThumb(escape(goods.getGoodsThumb()));
        goods.setCreateBy(AdminContext.getLoginUserId());
        // status 不传则默认 0 下架
        if (goods.getStatus() == null) {
            goods.setStatus(0);
        }
        save(goods);

        // 记录操作日志
        saveOperateLog(goods.getId(), 1, dto);
        log.info("[商品管理] 新增商品成功，id={}, goodsSn={}, 操作人={}",
                goods.getId(), goods.getGoodsSn(), goods.getCreateBy());
        return Response.ok("新增商品成功", null);
    }

    @Override
    public Response updateGoods(GoodsUpdateDTO dto) {
        Goods existGoods = getById(dto.getId());
        if (existGoods == null) {
            return Response.fail(500, "商品不存在");
        }
        // 货号唯一性预校验（排除自身）
        if (existsByGoodsSn(dto.getGoodsSn(), dto.getId())) {
            return Response.fail(500, "商品货号已存在");
        }
        Goods goods = new Goods();
        BeanConvertUtils.copyProperties(dto, goods);
        // 兜底 XSS 防护：转义字符串字段
        goods.setGoodsName(escape(goods.getGoodsName()));
        goods.setGoodsSn(escape(goods.getGoodsSn()));
        goods.setGoodsThumb(escape(goods.getGoodsThumb()));
        goods.setUpdateBy(AdminContext.getLoginUserId());
        updateById(goods);

        // 记录操作日志
        saveOperateLog(dto.getId(), 2, dto);
        log.info("[商品管理] 修改商品成功，id={}, 操作人={}", dto.getId(), goods.getUpdateBy());
        return Response.ok("修改商品成功", null);
    }

    @Override
    public Response updateStatus(GoodsStatusDTO dto) {
        Goods existGoods = getById(dto.getId());
        if (existGoods == null) {
            return Response.fail(500, "商品不存在");
        }
        Goods goods = new Goods();
        goods.setId(dto.getId());
        goods.setStatus(Boolean.TRUE.equals(dto.getStatus()) ? 1 : 0);
        goods.setUpdateBy(AdminContext.getLoginUserId());
        updateById(goods);

        // 记录操作日志：4=上下架，记录变更前后状态
        Map<String, Object> content = new LinkedHashMap<>();
        content.put("beforeStatus", existGoods.getStatus() == 1);
        content.put("afterStatus", dto.getStatus());
        saveOperateLog(dto.getId(), 4, content);
        log.info("[商品管理] 商品上下架成功，id={}, {}->{}，操作人={}",
                dto.getId(), existGoods.getStatus() == 1, dto.getStatus(), goods.getUpdateBy());
        return Response.ok("商品上下架成功", null);
    }

    @Override
    public Response deleteGoods(Long id) {
        Goods existGoods = getById(id);
        if (existGoods == null) {
            return Response.fail(500, "商品不存在");
        }
        // 逻辑删除（@TableLogic 注解生效，自动追加 is_deleted 条件并将 is_deleted 置 1）
        removeById(id);

        // 记录操作日志
        saveOperateLog(id, 3, null);
        log.info("[商品管理] 删除商品成功（逻辑删除），id={}", id);
        return Response.ok("删除商品成功", null);
    }

    // ====================== 私有方法 ======================

    /**
     * 货号唯一性校验（排除指定商品ID，新增时传 null）
     */
    private boolean existsByGoodsSn(String goodsSn, Long excludeId) {
        LambdaQueryWrapper<Goods> wrapper = new LambdaQueryWrapper<Goods>()
                .eq(Goods::getGoodsSn, goodsSn);
        if (excludeId != null) {
            wrapper.ne(Goods::getId, excludeId);
        }
        return count(wrapper) > 0;
    }

    /**
     * 记录商品操作日志
     *
     * @param goodsId     商品ID
     * @param operateType 操作类型 1新增 2编辑 3删除 4上下架
     * @param content     变更内容（将被序列化为JSON存储）
     */
    private void saveOperateLog(Long goodsId, Integer operateType, Object content) {
        GoodsOperateLog log = new GoodsOperateLog();
        log.setGoodsId(goodsId);
        log.setAdminId(AdminContext.getLoginUserId());
        log.setOperateType(operateType);
        log.setContent(content == null ? null : JSON.toJSONString(content));
        goodsOperateLogMapper.insert(log);
    }

    /**
     * XSS 兜底防护：转义 HTML 特殊字符。
     * DTO 层 @Pattern 已拒绝 < >，此处再转义双引号/单引号/&，防止绕过。
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
