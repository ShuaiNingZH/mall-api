package com.atguigu.meet.controller.goods.list;

import com.atguigu.meet.annotation.RequirePermission;
import com.atguigu.meet.common.Response;
import com.atguigu.meet.constant.PermissionConst;
import com.atguigu.meet.model.dto.goods.list.GoodsPageQueryDTO;
import com.atguigu.meet.model.dto.goods.list.GoodsSaveDTO;
import com.atguigu.meet.model.dto.goods.list.GoodsStatusDTO;
import com.atguigu.meet.model.dto.goods.list.GoodsUpdateDTO;
import com.atguigu.meet.service.goods.list.GoodsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 商品列表
 */
@RestController
@RequestMapping("/goods")
@Validated
public class GoodsController {
    @Autowired
    private GoodsService goodsService;

    /** 商品分页列表 */
    @GetMapping
    @RequirePermission(PermissionConst.GOODS_QUERY)
    public Response getPageList(@Valid GoodsPageQueryDTO parameter) {
        return goodsService.getPageList(parameter);
    }

    /** 根据ID查商品 */
    @GetMapping("/{id}")
    @RequirePermission(PermissionConst.GOODS_QUERY)
    public Response getGoodsById(@PathVariable Long id) {
        return goodsService.getGoodsById(id);
    }

    /** 新增商品 */
    @PostMapping
    @RequirePermission(PermissionConst.GOODS_ADD)
    public Response addGoods(@RequestBody @Valid GoodsSaveDTO dto) {
        return goodsService.addGoods(dto);
    }

    /** 修改商品 */
    @PutMapping
    @RequirePermission(PermissionConst.GOODS_UPDATE)
    public Response updateGoods(@RequestBody @Valid GoodsUpdateDTO dto) {
        return goodsService.updateGoods(dto);
    }

    /** 商品上下架 */
    @PatchMapping("/status")
    @RequirePermission(PermissionConst.GOODS_SHELF)
    public Response updateStatus(@RequestBody @Valid GoodsStatusDTO dto) {
        return goodsService.updateStatus(dto);
    }

    /** 删除商品 */
    @DeleteMapping("/{id}")
    @RequirePermission(PermissionConst.GOODS_DELETE)
    public Response deleteGoods(@PathVariable Long id) {
        return goodsService.deleteGoods(id);
    }
}
