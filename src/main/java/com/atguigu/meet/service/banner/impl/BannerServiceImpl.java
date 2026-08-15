package com.atguigu.meet.service.banner.impl;

import com.atguigu.meet.common.Response;
import com.atguigu.meet.mapper.banner.BannerMapper;
import com.atguigu.meet.model.dto.banner.BannerPageQueryDTO;
import com.atguigu.meet.model.dto.banner.BannerSaveDTO;
import com.atguigu.meet.model.dto.banner.BannerUpdateDTO;
import com.atguigu.meet.model.entity.banner.Banner;
import com.atguigu.meet.model.vo.PageResultVO;
import com.atguigu.meet.service.banner.BannerService;
import com.atguigu.meet.utils.AdminContext;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 轮播图管理 Service 实现
 */
@Service
@Slf4j
public class BannerServiceImpl extends ServiceImpl<BannerMapper, Banner> implements BannerService {

    @Override
    public Response getPageList(BannerPageQueryDTO parameter) {
        LambdaQueryWrapper<Banner> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(parameter.getPosition())) {
            wrapper.eq(Banner::getPosition, parameter.getPosition());
        }
        if (parameter.getStatus() != null) {
            wrapper.eq(Banner::getStatus, parameter.getStatus());
        }
        if (parameter.getStartTime() != null) {
            wrapper.ge(Banner::getCreatedAt, parameter.getStartTime());
        }
        if (parameter.getEndTime() != null) {
            wrapper.le(Banner::getCreatedAt, parameter.getEndTime());
        }
        // sort 越大越靠前，同级按创建时间倒序
        wrapper.orderByDesc(Banner::getSort);
        wrapper.orderByDesc(Banner::getCreatedAt);

        IPage<Banner> page = new Page<>(parameter.getPageNum(), parameter.getPageSize());
        IPage<Banner> result = page(page, wrapper);
        return Response.ok(PageResultVO.of(result));
    }

    @Override
    public Response getBannerById(Long id) {
        Banner banner = getById(id);
        if (banner == null) {
            return Response.fail(500, "轮播图不存在");
        }
        return Response.ok(banner);
    }

    @Override
    public Response getEnabledBannersByPosition(String position) {
        LambdaQueryWrapper<Banner> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Banner::getStatus, 1);
        if (StringUtils.hasText(position)) {
            wrapper.eq(Banner::getPosition, position);
        }
        wrapper.orderByDesc(Banner::getSort);
        wrapper.orderByDesc(Banner::getCreatedAt);
        List<Banner> banners = list(wrapper);
        return Response.ok(banners);
    }

    @Override
    public Response addBanner(BannerSaveDTO dto) {
        Banner banner = new Banner();
        BeanUtils.copyProperties(dto, banner);
        // 操作人 = 当前登录管理员
        banner.setCreateBy(AdminContext.getLoginUserId());
        // createdAt/updatedAt 由数据库默认值填充
        save(banner);
        log.info("[轮播图管理] 新增轮播图成功，id={}, position={}, 操作人={}", banner.getId(), banner.getPosition(), banner.getCreateBy());
        return Response.ok("新增轮播图成功", null);
    }

    @Override
    public Response updateBanner(BannerUpdateDTO dto) {
        Banner existBanner = getById(dto.getId());
        if (existBanner == null) {
            return Response.fail(500, "轮播图不存在");
        }
        Banner banner = new Banner();
        BeanUtils.copyProperties(dto, banner);
        banner.setUpdateBy(AdminContext.getLoginUserId());
        updateById(banner);
        log.info("[轮播图管理] 修改轮播图成功，id={}, 操作人={}", dto.getId(), banner.getUpdateBy());
        return Response.ok("修改轮播图成功", null);
    }

    @Override
    public Response deleteBanner(Long id) {
        Banner banner = getById(id);
        if (banner == null) {
            return Response.fail(500, "轮播图不存在");
        }
        // 逻辑删除（@TableLogic 注解生效）
        removeById(id);
        log.info("[轮播图管理] 删除轮播图成功，id={}", id);
        return Response.ok("删除轮播图成功", null);
    }
}
