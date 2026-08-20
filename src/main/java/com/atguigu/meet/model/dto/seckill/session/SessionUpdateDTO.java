package com.atguigu.meet.model.dto.seckill.session;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 抢购场次修改DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SessionUpdateDTO extends SessionSaveDTO {
    @NotNull(message = "场次ID不能为空")
    private Long id;
}
