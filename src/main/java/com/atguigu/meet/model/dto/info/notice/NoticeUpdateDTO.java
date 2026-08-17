package com.atguigu.meet.model.dto.info.notice;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 公告修改DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class NoticeUpdateDTO extends NoticeSaveDTO {
    @NotNull(message = "公告ID不能为空")
    private Long id;
}