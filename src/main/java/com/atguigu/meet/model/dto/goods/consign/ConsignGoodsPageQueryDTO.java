package com.atguigu.meet.model.dto.goods.consign;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 抢购托售商品分页查询DTO
 */
@Data
public class ConsignGoodsPageQueryDTO {
    @NotNull(message = "分页页码不能为空")
    private Integer pageNum;
    @NotNull(message = "每页条数不能为空")
    private Integer pageSize;

    /** 商品名称（模糊查询） */
    private String goodsName;
    /** 委托人ID（精确查询） */
    private Long memberId;
    /** 所属场次ID（精确查询） */
    private Long sessionId;
    /** 商品业务状态 1-6 */
    private Integer goodsStatus;
    /** 上下架状态 0下架 1上架 */
    private Integer onlineStatus;
    /** 创建开始时间（含） */
    private LocalDateTime startTime;
    /** 创建结束时间（含） */
    private LocalDateTime endTime;
}
