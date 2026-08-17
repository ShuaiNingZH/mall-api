package com.atguigu.meet.model.dto.info.banner;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 轮播图修改DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BannerUpdateDTO extends BannerSaveDTO {
    @NotNull(message = "轮播图ID不能为空")
    private Long id;
}