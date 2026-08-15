package com.atguigu.meet.model.dto.banner;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 轮播图新增DTO
 */
@Data
public class BannerSaveDTO {

    @NotBlank(message = "轮播图地址不能为空")
    private String imgUrl;

    /** 轮播位置：home=首页 seckill=抢购 */
    @NotBlank(message = "轮播位置不能为空")
    private String position;

    /** 权重，越大越靠前 */
    private Integer sort;

    /** 跳转url */
    private String linkValue;

    /** 状态：0-禁用，1-启用 */
    private Integer status;
}
