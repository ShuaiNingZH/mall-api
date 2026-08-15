package com.atguigu.meet.service.banner;

import com.atguigu.meet.common.Response;
import com.atguigu.meet.model.dto.banner.BannerPageQueryDTO;
import com.atguigu.meet.model.dto.banner.BannerSaveDTO;
import com.atguigu.meet.model.dto.banner.BannerUpdateDTO;

/**
 * 轮播图管理 Service
 */
public interface BannerService {

    /** 轮播图分页列表 */
    Response getPageList(BannerPageQueryDTO parameter);

    /** 根据ID查轮播图 */
    Response getBannerById(Long id);

    /** 按位置获取所有启用轮播图（C端展示用） */
    Response getEnabledBannersByPosition(String position);

    /** 新增轮播图 */
    Response addBanner(BannerSaveDTO dto);

    /** 修改轮播图 */
    Response updateBanner(BannerUpdateDTO dto);

    /** 删除轮播图（逻辑删除） */
    Response deleteBanner(Long id);
}
