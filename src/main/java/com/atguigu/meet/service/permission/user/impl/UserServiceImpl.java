package com.atguigu.meet.service.permission.user.impl;

import com.atguigu.meet.common.Response;
import com.atguigu.meet.exception.BusinessException;
import com.atguigu.meet.mapper.permission.menu.SysMenuMapper;
import com.atguigu.meet.mapper.permission.user.UserMapper;
import com.atguigu.meet.model.dto.permission.user.UserDeleteDTO;
import com.atguigu.meet.model.dto.permission.user.UserPageQueryDTO;
import com.atguigu.meet.model.dto.permission.user.UserUpdateDTO;
import com.atguigu.meet.model.entity.permission.menu.SysMenu;
import com.atguigu.meet.model.entity.permission.user.AdminUser;
import com.atguigu.meet.model.entity.permission.user.SysUser;
import com.atguigu.meet.model.vo.PageResultVO;
import com.atguigu.meet.model.vo.permission.menu.MenuVO;
import com.atguigu.meet.model.vo.permission.user.UserOrderVO;
import com.atguigu.meet.model.vo.permission.user.UserVO;
import com.atguigu.meet.service.file.FileService;
import com.atguigu.meet.service.permission.user.UserService;
import com.atguigu.meet.utils.AdminContext;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Description
 * @Date 2026-08-12 23:59
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, SysUser> implements UserService {
    @Autowired
    private FileService fileService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private SysMenuMapper sysMenuMapper;

    @Override
    @Transactional(rollbackFor = Exception.class) // 所有异常都回滚，保证原子性
    public Response deleteUserByIds(UserDeleteDTO userDeleteDTO) {
        List<Long> idList = Arrays.asList(userDeleteDTO.getUserIds());
        List<SysUser> dbUserList = listByIds(idList);
        Set<Long> existIdSet = dbUserList.stream()
                .map(SysUser::getId)
                .collect(Collectors.toSet());
        List<Long> notExistIds = idList.stream()
                .filter(id -> !existIdSet.contains(id))
                .collect(Collectors.toList());
        if (!notExistIds.isEmpty()) {
            return Response.fail(500, "用户ID：" + notExistIds + " 不存在，本次全部取消删除");
        }

        removeByIds(idList);

        return Response.ok("成功删除" + idList.size() + "个用户", null);
    }

    @Override
    public Response updateUser(UserUpdateDTO userUpdateDTO) {
        Long userId = userUpdateDTO.getId();
        LambdaQueryWrapper<SysUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SysUser::getId, userId);
        SysUser existUser = getOne(lambdaQueryWrapper);
        if (existUser == null) {
            return Response.fail(500, "用户不存在");
        }
        SysUser user = new SysUser();
//        String encodePwd = passwordEncoder.encode(userUpdateDTO.getPassword());
        BeanUtils.copyProperties(userUpdateDTO, user);
//        user.setPassword(encodePwd);
        userMapper.updateById(user);
        return Response.ok("更新用户信息成功", null);
    }

    @Override
    public Response getUserByPhone(String phone, AdminUser loginAdmin) {
        if (loginAdmin != null && !loginAdmin.getPhone().equals(phone)) {
            throw new BusinessException("无权限查询其他用户信息");
        }
        LambdaQueryWrapper<SysUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SysUser::getPhone, phone);
        SysUser existUser = userMapper.selectOne(lambdaQueryWrapper);
        if (existUser == null) {
            return Response.fail(500, "用户不存在");
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(existUser, userVO);
        return Response.ok("查询用户成功", userVO);
    }

    @Override
    public Response getList() {
        LambdaQueryWrapper<SysUser> wrapper = Wrappers.lambdaQuery(SysUser.class);
        List<SysUser> userList = userMapper.selectList(wrapper);
        return Response.ok(userList);
    }

    @Override
    public Response getPageList(UserPageQueryDTO parameter) {
        LambdaQueryWrapper<SysUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();

        if (parameter.getAge() != null) {
            lambdaQueryWrapper.lt(SysUser::getAge, parameter.getAge());
        }

        if (parameter.getUsername() != null && StringUtils.hasText(parameter.getUsername())) {
            lambdaQueryWrapper.like(SysUser::getUsername, parameter.getUsername());
        }

        IPage<SysUser> page = new Page<>(parameter.getPageNum(), parameter.getPageSize());
        IPage<SysUser> result = page(page, lambdaQueryWrapper);
        // 方式一
        /*//封装统一返回格式（包含分页信息 + 数据）
        JSONObject resultData = new JSONObject();
        resultData.put("list", result.getRecords()); // 数据列表 List<User>
        resultData.put("total", result.getTotal()); // 总条数
        resultData.put("totalPages", result.getPages()); // 总页数
        resultData.put("pageNum", result.getCurrent()); // 当前页
        resultData.put("pageSize", result.getSize()); // 每页条数
        return Response.ok(resultData);*/
        // 方式二(推荐)
        PageResultVO<SysUser> pageVO = PageResultVO.of(result);
        return Response.ok(pageVO);
    }

    @Override
    public void exportUserToCsv(HttpServletResponse response) {
// 1. 设置响应头，让浏览器下载文件
        response.setContentType("text/csv");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setHeader("Content-Disposition", "attachment; filename=user_list.csv");

        try (
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(response.getOutputStream(), StandardCharsets.UTF_8));
                CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader("ID", "用户名", "昵称", "邮箱", "手机号", "性别", "状态"))
        ) {
            // 2. 构建查询条件：只导出未删除的用户
            LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
//            wrapper.eq(SysUser::getIsDeleted, 0);

            // 3. 流式查询 + 边读边写
            this.baseMapper.selectList(wrapper, context -> {
                SysUser user = context.getResultObject();
                try {
                    // 把当前这条数据写入 CSV
                    csvPrinter.printRecord(
                            user.getId(),
                            user.getUsername(),
                            user.getNickname(),
                            user.getEmail(),
                            user.getPhone(),
                            user.getGender(),
                            user.getStatus()
                    );
                } catch (IOException e) {
                    throw new RuntimeException("写入CSV失败", e);
                }
            });

            csvPrinter.flush();
        } catch (Exception e) {
            throw new RuntimeException("导出用户数据失败", e);
        }
    }

    @Override
    public Response uploadUserAvatar(MultipartFile file, Long userId) {
        try {
            Response resUpload = fileService.upload(file, "avatar");
            if (resUpload.getCode() == 500) return resUpload;
            String url = (String) resUpload.getData();
            UserUpdateDTO userUpdateDTO = new UserUpdateDTO();
            userUpdateDTO.setId(userId);
            userUpdateDTO.setAvatar(url);
            updateUser(userUpdateDTO);
            return Response.ok("头像上传并更新成功", url);
        } catch (RuntimeException e) {
            throw new BusinessException(e.getMessage());
//            return Response.fail(500, e.getMessage());
        }
    }

    @Override
    public Response getUserWithOrders(String phone, AdminUser loginAdmin) {
        if (loginAdmin != null && !loginAdmin.getPhone().equals(phone)) {
            throw new BusinessException("无权限查询其他用户信息");
        }
        try {
            UserOrderVO userOrderVO = userMapper.getUserWithOrders(phone);
            return Response.ok(userOrderVO);
        } catch (RuntimeException e) {
            throw new BusinessException(e.getMessage());
        }
    }

    @Override
    public Response getCurrentUserInfo() {
        AdminUser currentUser = AdminContext.get();
        if (currentUser == null) {
            return Response.fail(401, "未登录");
        }
        SysUser user = userMapper.selectById(currentUser.getUserId());
        if (user == null) {
            return Response.fail(404, "用户不存在");
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        userVO.setPermissions(currentUser.getPermissions());
        return Response.ok(userVO);
    }

    @Override
    public Response getCurrentUserMenus() {
        AdminUser currentUser = AdminContext.get();
        if (currentUser == null) {
            return Response.fail(401, "未登录");
        }
        Long userId = currentUser.getUserId();
        Set<String> userPermissions = currentUser.getPermissions();

        List<SysMenu> allMenus = sysMenuMapper.selectMenusByUserId(userId);
        List<MenuVO> menuTree = filterMenuTree(buildMenuTree(allMenus, 0L), userPermissions);
        return Response.ok(menuTree);
    }

    private List<MenuVO> buildMenuTree(List<SysMenu> allMenus, Long parentId) {
        return allMenus.stream()
                .filter(m -> parentId.equals(m.getParentId()))
                .map(m -> {
                    MenuVO vo = new MenuVO();
                    BeanUtils.copyProperties(m, vo);
                    vo.setChildren(buildMenuTree(allMenus, m.getId()));
                    return vo;
                })
                .collect(Collectors.toList());
    }

    private List<MenuVO> filterMenuTree(List<MenuVO> tree, Set<String> userPermissions) {
        List<MenuVO> result = new ArrayList<>();
        for (MenuVO menu : tree) {
            if (menu.getType() == 2) {
                if (userPermissions.contains(menu.getPerm())) {
                    result.add(menu);
                }
            } else {
                List<MenuVO> filteredChildren = filterMenuTree(menu.getChildren(), userPermissions);
                menu.setChildren(filteredChildren);
                if (menu.getType() == 0 || menu.getType() == 1) {
                    if (!filteredChildren.isEmpty()) {
                        result.add(menu);
                    } else if (menu.getType() == 1 && (menu.getPath() != null && !menu.getPath().isEmpty())) {
                        result.add(menu);
                    }
                }
            }
        }
        return result;
    }

    /*public Response exportAllUser() {
        LambdaQueryWrapper<SysUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        userMapper.selectList(lambdaQueryWrapper, new ResultHandler<SysUser>() {
            @Override
            public void handleResult(ResultContext<? extends SysUser> resultContext) {
                SysUser user = resultContext.getResultObject();
                if (resultContext.getResultCount() >= 100) {
                    resultContext.stop();
                }
            }
        });
    }*/

   /* @Override
    public List<Map<String, Object>> mapList() {
        LambdaQueryWrapper<SysUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SysUser::getAge, 10);
        List<Map<String, Object>> mapList = listMaps(lambdaQueryWrapper);
        return mapList;
    }

    @Override
    public List<Object> idList() {
        LambdaQueryWrapper<SysUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SysUser::getAge, 10);
        return listObjs(lambdaQueryWrapper, (obj) -> String.valueOf(obj));
    }*/
}