package com.atguigu.meet.controller.system;

import com.atguigu.meet.annotation.RequirePermission;
import com.atguigu.meet.common.Response;
import com.atguigu.meet.constant.PermissionConst;
import com.atguigu.meet.model.dto.system.MenuPageQueryDTO;
import com.atguigu.meet.model.dto.system.MenuSaveDTO;
import com.atguigu.meet.model.dto.system.MenuUpdateDTO;
import com.atguigu.meet.service.system.MenuService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 菜单管理接口
 */
@RestController
@RequestMapping("/menus")
@Validated
public class MenuController {
    @Autowired
    private MenuService menuService;

    /** 菜单树形列表 */
    @GetMapping("/tree")
    @RequirePermission(PermissionConst.MENU_QUERY)
    public Response getMenuTree() {
        return menuService.getMenuTree();
    }

    /** 菜单平铺分页列表 */
    @GetMapping
    @RequirePermission(PermissionConst.MENU_QUERY)
    public Response getPageList(@Valid MenuPageQueryDTO parameter) {
        return menuService.getPageList(parameter);
    }

    /** 所有菜单（平铺，角色分配菜单用） */
    @GetMapping("/all")
    @RequirePermission(PermissionConst.MENU_QUERY)
    public Response getAllMenus() {
        return menuService.getAllMenus();
    }

    /** 根据ID查菜单 */
    @GetMapping("/{id}")
    @RequirePermission(PermissionConst.MENU_QUERY)
    public Response getMenuById(@PathVariable Long id) {
        return menuService.getMenuById(id);
    }

    /** 新增菜单 */
    @PostMapping
    @RequirePermission(PermissionConst.MENU_ADD)
    public Response addMenu(@RequestBody @Valid MenuSaveDTO dto) {
        return menuService.addMenu(dto);
    }

    /** 修改菜单 */
    @PutMapping
    @RequirePermission(PermissionConst.MENU_UPDATE)
    public Response updateMenu(@RequestBody @Valid MenuUpdateDTO dto) {
        return menuService.updateMenu(dto);
    }

    /** 删除菜单（递归删除子菜单） */
    @DeleteMapping("/{id}")
    @RequirePermission(PermissionConst.MENU_DELETE)
    public Response deleteMenu(@PathVariable Long id) {
        return menuService.deleteMenu(id);
    }
}
