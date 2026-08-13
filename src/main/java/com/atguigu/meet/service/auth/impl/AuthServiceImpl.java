package com.atguigu.meet.service.auth.impl;

import com.atguigu.meet.common.Response;
import com.atguigu.meet.exception.BusinessException;
import com.atguigu.meet.mapper.user.UserMapper;
import com.atguigu.meet.mapper.system.SysPermissionMapper;
import com.atguigu.meet.model.dto.auth.AuthRegisterDTO;
import com.atguigu.meet.model.dto.auth.AuthLoginDTO;
import com.atguigu.meet.model.entity.user.SysUser;
import com.atguigu.meet.model.vo.user.UserVO;
import com.atguigu.meet.service.auth.AuthService;
import com.atguigu.meet.utils.JwtUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description
 * @Date 2026-08-12 23:57
 */
@Service
@Slf4j
public class AuthServiceImpl extends ServiceImpl<UserMapper, SysUser> implements AuthService {
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private SysPermissionMapper sysPermissionMapper;

    @Override
    public Response register(AuthRegisterDTO authRegisterDTO) {
        LambdaQueryWrapper<SysUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SysUser::getPhone, authRegisterDTO.getPhone());
        SysUser existUser = getOne(lambdaQueryWrapper);
        if (existUser != null) {
            return Response.fail(500, "用户已存在");
        }
        SysUser user = new SysUser();
        String encodePwd = passwordEncoder.encode(authRegisterDTO.getPassword());
        BeanUtils.copyProperties(authRegisterDTO, user);
        user.setPassword(encodePwd);
        userMapper.insert(user);
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return Response.ok("创建用户成功", userVO);
    }

    @Override
    public Response login(AuthLoginDTO authLoginDTO) {
        String account = authLoginDTO.getAccount();
        LambdaQueryWrapper<SysUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SysUser::getPhone, account);
        SysUser existUser = getOne(lambdaQueryWrapper);
        if (existUser == null) throw new BusinessException("当前用户不存在");
        boolean bool = passwordEncoder.matches(authLoginDTO.getPassword(), existUser.getPassword());
        if (!bool) throw new BusinessException("用户账号密码不正确");
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", existUser.getUsername());
        claims.put("nickname", existUser.getNickname());
//        claims.put("birthday", existUser.getBirthday());
        claims.put("phone", existUser.getPhone());
        claims.put("status", existUser.getStatus());
        // 查询当前用户的权限码列表，写入 JWT（无状态授权）
        List<String> permissions = sysPermissionMapper.selectPermissionCodesByUserId(existUser.getId());
        claims.put("permissions", permissions);
        String token = jwtUtil.generateToken(existUser.getId(), claims);
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        return Response.ok(200, "用户登录成功", data);
    }

}
