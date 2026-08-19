package com.atguigu.meet.service.permission.role.impl;

import com.atguigu.meet.common.Response;
import com.atguigu.meet.exception.BusinessException;
import com.atguigu.meet.mapper.permission.role.SysRoleMapper;
import com.atguigu.meet.mapper.permission.role.SysRoleMenuMapper;
import com.atguigu.meet.model.dto.permission.role.RoleAssignMenuDTO;
import com.atguigu.meet.model.dto.permission.role.RolePageQueryDTO;
import com.atguigu.meet.model.dto.permission.role.RoleSaveDTO;
import com.atguigu.meet.model.dto.permission.role.RoleUpdateDTO;
import com.atguigu.meet.model.entity.permission.role.SysRole;
import com.atguigu.meet.model.entity.permission.role.SysRoleMenu;
import com.atguigu.meet.model.vo.PageResultVO;
import com.atguigu.meet.model.vo.permission.role.RoleVO;
import com.atguigu.meet.service.auth.PermissionCacheService;
import com.atguigu.meet.service.permission.role.RoleService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import com.atguigu.meet.utils.BeanConvertUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色管理 Service 实现
 */
@Service
@Slf4j
public class RoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements RoleService {

    @Autowired
    private SysRoleMenuMapper sysRoleMenuMapper;

    @Autowired
    private PermissionCacheService permissionCacheService;

    @Override
    public Response getPageList(RolePageQueryDTO parameter) {
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(parameter.getRoleName())) {
            wrapper.like(SysRole::getRoleName, parameter.getRoleName());
        }
        if (StringUtils.hasText(parameter.getRoleCode())) {
            wrapper.like(SysRole::getRoleCode, parameter.getRoleCode());
        }
        if (parameter.getStatus() != null) {
            wrapper.eq(SysRole::getStatus, parameter.getStatus());
        }
        wrapper.orderByDesc(SysRole::getCreateTime);

        IPage<SysRole> page = new Page<>(parameter.getPageNum(), parameter.getPageSize());
        IPage<SysRole> result = page(page, wrapper);
        return Response.ok(PageResultVO.of(result));
    }

    @Override
    public Response getAllRoles() {
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRole::getStatus, 1);
        wrapper.orderByDesc(SysRole::getCreateTime);
        List<SysRole> roles = list(wrapper);
        return Response.ok(roles);
    }

    @Override
    public Response getRoleById(Long id) {
        SysRole role = getById(id);
        if (role == null) {
            return Response.fail(500, "角色不存在");
        }
        RoleVO vo = new RoleVO();
        BeanConvertUtils.copyProperties(role, vo);
        // 查询角色已分配的菜单ID列表
        List<Long> menuIds = sysRoleMenuMapper.selectMenuIdsByRoleId(id);
        vo.setMenuIds(menuIds);
        return Response.ok(vo);
    }

    @Override
    public Response addRole(RoleSaveDTO dto) {
        // 校验角色编码唯一
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRole::getRoleCode, dto.getRoleCode());
        if (count(wrapper) > 0) {
            return Response.fail(500, "角色编码已存在");
        }
        SysRole role = new SysRole();
        BeanConvertUtils.copyProperties(dto, role);
        save(role);
        return Response.ok("新增角色成功", null);
    }

    @Override
    public Response updateRole(RoleUpdateDTO dto) {
        SysRole existRole = getById(dto.getId());
        if (existRole == null) {
            return Response.fail(500, "角色不存在");
        }
        // 如果修改了角色编码，校验唯一性
        if (!existRole.getRoleCode().equals(dto.getRoleCode())) {
            LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysRole::getRoleCode, dto.getRoleCode());
            wrapper.ne(SysRole::getId, dto.getId());
            if (count(wrapper) > 0) {
                return Response.fail(500, "角色编码已存在");
            }
        }
        SysRole role = new SysRole();
        BeanConvertUtils.copyProperties(dto, role);
        updateById(role);
        return Response.ok("修改角色成功", null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response deleteRole(Long id) {
        SysRole role = getById(id);
        if (role == null) {
            return Response.fail(500, "角色不存在");
        }
        // 删除角色
        removeById(id);
        // 删除角色-菜单关联
        sysRoleMenuMapper.deleteByRoleId(id);
        log.info("[角色管理] 删除角色成功，roleId={}, roleName={}", id, role.getRoleName());
        return Response.ok("删除角色成功", null);
    }

    @Override
    public Response getRoleMenuIds(Long roleId) {
        List<Long> menuIds = sysRoleMenuMapper.selectMenuIdsByRoleId(roleId);
        return Response.ok(menuIds);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response assignMenus(RoleAssignMenuDTO dto) {
        SysRole role = getById(dto.getRoleId());
        if (role == null) {
            return Response.fail(500, "角色不存在");
        }

        // 先删除旧关联
        sysRoleMenuMapper.deleteByRoleId(dto.getRoleId());

        // 再批量插入新关联
        if (dto.getMenuIds() != null && !dto.getMenuIds().isEmpty()) {
            List<SysRoleMenu> roleMenus = dto.getMenuIds().stream().map(menuId -> {
                SysRoleMenu rm = new SysRoleMenu();
                rm.setRoleId(dto.getRoleId());
                rm.setMenuId(menuId);
                return rm;
            }).collect(Collectors.toList());

            for (SysRoleMenu rm : roleMenus) {
                sysRoleMenuMapper.insert(rm);
            }
        }

        // 清除该角色下所有用户的权限缓存
        permissionCacheService.invalidateAllPermissions();

        log.info("[角色管理] 分配菜单成功，roleId={}, menuIds={}", dto.getRoleId(), dto.getMenuIds());
        return Response.ok("分配菜单成功", null);
    }
}