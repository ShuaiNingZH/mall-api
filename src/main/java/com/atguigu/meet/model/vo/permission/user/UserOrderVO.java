package com.atguigu.meet.model.vo.permission.user;

import com.atguigu.meet.model.entity.Orders;
import lombok.Data;

import java.util.List;

/**
 * @Description
 * @Date 2026-05-29 15:10
 */
@Data
public class UserOrderVO {
    private Long userId;
    private String username;
    private String phone;
    private List<Orders> ordersList;
}