package com.atguigu.meet.controller.user;

import com.atguigu.meet.annotation.RequirePermission;
import com.atguigu.meet.common.Response;
import com.atguigu.meet.constant.PermissionConst;
import com.atguigu.meet.model.dto.user.UserDeleteDTO;
import com.atguigu.meet.model.dto.user.UserPageQueryDTO;
import com.atguigu.meet.model.dto.user.UserUpdateDTO;
import com.atguigu.meet.service.user.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 用户管理接口
 * <p>
 * 权限标识统一使用 {@link PermissionConst} 常量维护
 */
@RestController
@RequestMapping("/users")
@Validated
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * 用户列表分页查询
     */
    @GetMapping
    @RequirePermission(PermissionConst.USER_QUERY)
    public Response pageList(@Valid UserPageQueryDTO parameter) {
        return userService.getPageList(parameter);
    }

    /**
     * 批量删除用户
     */
    @DeleteMapping
    @RequirePermission(PermissionConst.USER_DELETE)
    public Response deleteUser(@RequestBody @Valid UserDeleteDTO userDeleteDTO) {
        return userService.deleteUserByIds(userDeleteDTO);
    }

    /**
     * 更新用户信息
     */
    @PutMapping
    @RequirePermission(PermissionConst.USER_UPDATE)
    public Response updateUser(@RequestBody @Valid UserUpdateDTO userUpdateDTO) {
        return userService.updateUser(userUpdateDTO);
    }

    /**
     * 上传用户头像
     */
    @PostMapping("avatar/{userId}")
    @RequirePermission(PermissionConst.USER_UPDATE)
    public Response uploadUserAvatar(
            @PathVariable Long userId,
            @RequestParam("file") MultipartFile file
    ) {
        return userService.uploadUserAvatar(file, userId);
    }
}
