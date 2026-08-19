package com.atguigu.meet.service.permission.user;

import com.atguigu.meet.common.Response;
import com.atguigu.meet.model.dto.permission.user.UserDeleteDTO;
import com.atguigu.meet.model.dto.permission.user.UserPageQueryDTO;
import com.atguigu.meet.model.dto.permission.user.UserStatusDTO;
import com.atguigu.meet.model.dto.permission.user.UserUpdateDTO;
import com.atguigu.meet.model.entity.permission.user.AdminUser;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Description
 * @Date 2026-08-12 23:59
 */
public interface UserService {
    Response deleteUserByIds(UserDeleteDTO userDeleteDTO);

    Response updateUser(UserUpdateDTO userUpdateDTO);

    /** 启用/禁用用户 */
    Response updateStatus(UserStatusDTO userStatusDTO);

    Response getUserByPhone(String phone, AdminUser loginAdmin);

    Response getList();

    Response getPageList(UserPageQueryDTO parameter);

    void exportUserToCsv(HttpServletResponse response);

    Response uploadUserAvatar(MultipartFile file, String platform);

    Response getUserWithOrders(String phone, AdminUser loginAdmin);

    /** 获取当前登录用户信息 */
    Response getCurrentUserInfo();

    /** 获取当前登录用户可访问的菜单树 */
    Response getCurrentUserMenus();

    /*List<Map<String, Object>> mapList();

    List<Object> idList();*/
}