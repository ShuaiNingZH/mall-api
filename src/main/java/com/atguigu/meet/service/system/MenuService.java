package com.atguigu.meet.service.system;

import com.atguigu.meet.common.Response;
import com.atguigu.meet.model.dto.system.MenuPageQueryDTO;
import com.atguigu.meet.model.dto.system.MenuSaveDTO;
import com.atguigu.meet.model.dto.system.MenuUpdateDTO;
import com.atguigu.meet.model.entity.system.SysMenu;

import java.util.List;

/**
 * 菜单管理 Service
 */
public interface MenuService {

    /** 菜单树形列表（全量） */
    Response getMenuTree();

    /** 菜单平铺分页列表 */
    Response getPageList(MenuPageQueryDTO parameter);

    /** 根据ID查菜单 */
    Response getMenuById(Long id);

    /** 新增菜单 */
    Response addMenu(MenuSaveDTO dto);

    /** 修改菜单 */
    Response updateMenu(MenuUpdateDTO dto);

    /** 删除菜单（递归删除子菜单） */
    Response deleteMenu(Long id);

    /** 获取所有菜单（平铺，供角色分配菜单时使用） */
    Response getAllMenus();
}
