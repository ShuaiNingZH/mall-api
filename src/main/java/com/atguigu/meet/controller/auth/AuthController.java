package com.atguigu.meet.controller.auth;

import com.atguigu.meet.common.Response;
import com.atguigu.meet.model.dto.auth.AuthRegisterDTO;
import com.atguigu.meet.model.dto.auth.AuthLoginDTO;
import com.atguigu.meet.service.auth.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description
 * @Date 2026-08-12 22:59
 */
@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("login")
    public Response login(@RequestBody @Valid AuthLoginDTO user) {
        return authService.login(user);
    }

    @PostMapping("register")
    public Response register(@RequestBody @Valid AuthRegisterDTO user) {
        return authService.register(user);
    }
}
