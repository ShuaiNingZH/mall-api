package com.atguigu.meet.controller.user;

import com.atguigu.meet.common.Response;
import com.atguigu.meet.model.dto.user.UserDeleteDTO;
import com.atguigu.meet.model.dto.user.UserPageQueryDTO;
import com.atguigu.meet.model.dto.user.UserUpdateDTO;
import com.atguigu.meet.service.user.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Description
 * @Date 2026-08-12 23:05
 */
@RestController
@RequestMapping("/users")
@Validated
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping
    @PreAuthorize("hasAuthority('user:query')")
    public Response pageList(@Valid UserPageQueryDTO parameter) {
        return userService.getPageList(parameter);
    }

    // 假删除
    @DeleteMapping
    @PreAuthorize("hasAuthority('user:delete')")
    public Response deleteUser(@RequestBody @Valid UserDeleteDTO userDeleteDTO) {
        return userService.deleteUserByIds(userDeleteDTO);
    }

    @PutMapping
    @PreAuthorize("hasAuthority('user:update')")
    public Response updateUser(@RequestBody @Valid UserUpdateDTO userUpdateDTO) {
        return userService.updateUser(userUpdateDTO);
    }

    @PostMapping("avatar/{userId}")
    @PreAuthorize("hasAuthority('user:update')")
    public Response uploadUserAvatar(
            @PathVariable Long userId,
            @RequestParam("file") MultipartFile file
    ) {
        return userService.uploadUserAvatar(file, userId);
    }
}
