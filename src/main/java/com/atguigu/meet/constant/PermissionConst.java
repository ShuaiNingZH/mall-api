package com.atguigu.meet.constant;

/**
 * 权限标识常量类
 * <p>
 * 所有权限标识必须与数据库 sys_menu.perms 字段保持一致。
 * 建议 @RequirePermission 注解统一使用本类常量，避免手写字符串出错。
 * <p>
 * 命名规则：模块名_操作名 ，全部大写，下划线分隔
 * 值规则：模块名:操作名 ，全小写，冒号分隔
 */
public final class PermissionConst {

    private PermissionConst() {
    }

    // ==========================================
    // 用户管理模块 (sys_menu.menu_code = user)
    // ==========================================
    /** 用户查询 */
    public static final String USER_QUERY = "user:query";
    /** 用户新增 */
    public static final String USER_ADD = "user:add";
    /** 用户修改 */
    public static final String USER_UPDATE = "user:update";
    /** 用户删除 */
    public static final String USER_DELETE = "user:delete";

    // ==========================================
    // 角色管理模块 (sys_menu.menu_code = role)
    // 提前预留，便于后续角色管理接口直接复用
    // ==========================================
    /** 角色查询 */
    public static final String ROLE_QUERY = "role:query";
    /** 角色新增 */
    public static final String ROLE_ADD = "role:add";
    /** 角色修改 */
    public static final String ROLE_UPDATE = "role:update";
    /** 角色删除 */
    public static final String ROLE_DELETE = "role:delete";
    /** 角色分配权限 */
    public static final String ROLE_ASSIGN_MENU = "role:assign:menu";
    /** 角色分配用户 */
    public static final String ROLE_ASSIGN_USER = "role:assign:user";

    // ==========================================
    // 菜单管理模块 (sys_menu.menu_code = menu)
    // ==========================================
    /** 菜单查询 */
    public static final String MENU_QUERY = "menu:query";
    /** 菜单新增 */
    public static final String MENU_ADD = "menu:add";
    /** 菜单修改 */
    public static final String MENU_UPDATE = "menu:update";
    /** 菜单删除 */
    public static final String MENU_DELETE = "menu:delete";

    // ==========================================
    // 系统管理模块 (sys_menu.menu_code = sys)
    // ==========================================
    /** 系统配置查询 */
    public static final String SYS_CONFIG_QUERY = "sys:config:query";
    /** 系统配置修改 */
    public static final String SYS_CONFIG_UPDATE = "sys:config:update";
    /** 系统日志查询 */
    public static final String SYS_LOG_QUERY = "sys:log:query";

    // ==========================================
    // 文件管理模块
    // ==========================================
    /** 文件上传 */
    public static final String FILE_UPLOAD = "file:upload";
    /** 文件下载 */
    public static final String FILE_DOWNLOAD = "file:download";
    /** 文件删除 */
    public static final String FILE_DELETE = "file:delete";
}
