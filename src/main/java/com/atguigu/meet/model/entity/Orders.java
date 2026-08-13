package com.atguigu.meet.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @Description
 * @Date 2026-05-29 14:45
 */
@Data
@TableName("orders")
public class Orders extends Model<Orders> {
    private Long id;

    private String orderNo;

    private Long userId;

    private BigDecimal totalAmount;

    private Integer status;

    private LocalDateTime createTime;
}
