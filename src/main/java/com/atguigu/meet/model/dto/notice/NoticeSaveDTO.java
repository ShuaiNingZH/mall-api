package com.atguigu.meet.model.dto.notice;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 公告新增DTO
 */
@Data
public class NoticeSaveDTO {

    @NotBlank(message = "公告标题不能为空")
    private String title;

    @NotBlank(message = "公告内容不能为空")
    private String content;

    /** 排序，数值越大越靠前展示 */
    private Integer sort;

    /** 状态：0-禁用，1-启用 */
    private Integer status;
}
