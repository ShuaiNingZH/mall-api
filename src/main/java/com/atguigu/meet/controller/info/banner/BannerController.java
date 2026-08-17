package com.atguigu.meet.controller.info.banner;

import com.atguigu.meet.annotation.RequirePermission;
import com.atguigu.meet.common.Response;
import com.atguigu.meet.constant.PermissionConst;
import com.atguigu.meet.model.dto.info.banner.BannerPageQueryDTO;
import com.atguigu.meet.model.dto.info.banner.BannerSaveDTO;
import com.atguigu.meet.model.dto.info.banner.BannerUpdateDTO;
import com.atguigu.meet.service.info.banner.BannerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 轮播图管理接口
 */
@RestController
@RequestMapping("/banners")
@Validated
public class BannerController {
    @Autowired
    private BannerService bannerService;

    /** 轮播图分页列表 */
    @GetMapping
    @RequirePermission(PermissionConst.BANNER_QUERY)
    public Response getPageList(@Valid BannerPageQueryDTO parameter) {
        return bannerService.getPageList(parameter);
    }

    /** 按位置获取启用轮播图（C端展示用） */
    @GetMapping("/enabled")
    public Response getEnabledBanners(@RequestParam(required = false) String position) {
        return bannerService.getEnabledBannersByPosition(position);
    }

    /** 根据ID查轮播图 */
    @GetMapping("/{id}")
    @RequirePermission(PermissionConst.BANNER_QUERY)
    public Response getBannerById(@PathVariable Long id) {
        return bannerService.getBannerById(id);
    }

    /** 新增轮播图 */
    @PostMapping
    @RequirePermission(PermissionConst.BANNER_ADD)
    public Response addBanner(@RequestBody @Valid BannerSaveDTO dto) {
        return bannerService.addBanner(dto);
    }

    /** 修改轮播图 */
    @PutMapping
    @RequirePermission(PermissionConst.BANNER_UPDATE)
    public Response updateBanner(@RequestBody @Valid BannerUpdateDTO dto) {
        return bannerService.updateBanner(dto);
    }

    /** 删除轮播图（逻辑删除） */
    @DeleteMapping("/{id}")
    @RequirePermission(PermissionConst.BANNER_DELETE)
    public Response deleteBanner(@PathVariable Long id) {
        return bannerService.deleteBanner(id);
    }
}