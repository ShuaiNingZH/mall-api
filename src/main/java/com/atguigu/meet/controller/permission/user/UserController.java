package com.atguigu.meet.controller.permission.user;

import com.atguigu.meet.annotation.RequirePermission;
import com.atguigu.meet.common.Response;
import com.atguigu.meet.constant.PermissionConst;
import com.atguigu.meet.model.dto.permission.user.UserDeleteDTO;
import com.atguigu.meet.model.dto.permission.user.UserPageQueryDTO;
import com.atguigu.meet.model.dto.permission.user.UserUpdateDTO;
import com.atguigu.meet.service.permission.user.UserService;
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
     * 上传当前登录用户头像
     *
     * @param platform 存储平台: local-1 / aliyun-oss-1 / qiniu-kodo-1 / minio-1 / tencent-cos-1
     *                 为空时使用 application.yml 中 default-platform
     */
    @PostMapping("avatar")
    @RequirePermission(PermissionConst.USER_UPDATE)
    public Response uploadUserAvatar(@RequestParam("file") MultipartFile file,
                                     @RequestParam(value = "platform", required = false) String platform) {
        return userService.uploadUserAvatar(file, platform);
    }

    /**
     * 当前登录用户的信息
     */
    @GetMapping("user-info")
    public Response getCurrentUserInfo() {
        return userService.getCurrentUserInfo();
    }

    /**
     * 当前登录用户的菜单
     */
    @GetMapping("user-menus")
    public Response getCurrentUserMenus() {
        return userService.getCurrentUserMenus();
    }
}