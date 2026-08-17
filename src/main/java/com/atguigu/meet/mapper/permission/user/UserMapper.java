package com.atguigu.meet.mapper.permission.user;

import com.atguigu.meet.model.entity.permission.user.SysUser;
import com.atguigu.meet.model.vo.permission.user.UserOrderVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description
 * @Date 2026-04-22 11:05
 */
public interface UserMapper extends BaseMapper<SysUser> {
    SysUser selectByAccount(String account);

    // 根据用户 id 查询用户+所有订单
    UserOrderVO getUserWithOrders(String phone);
}