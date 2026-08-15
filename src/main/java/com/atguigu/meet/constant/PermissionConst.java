package com.atguigu.meet.constant;

/**
 * 权限标识常量类
 * <p>
 * 格式：前端模块:前端页面:按钮权限
 * 所有值必须与数据库 sys_menu.perms 字段保持一致。
 * 建议 @RequirePermission 注解统一使用本类常量，避免手写字符串出错。
 * <p>
 * 命名规则：页面_操作，全部大写，下划线分隔
 * 值规则：模块:页面:操作，全小写，冒号分隔
 */
public final class PermissionConst {

    private PermissionConst() {
    }

    // ==========================================
    // 系统管理 -> 用户管理 (sys:user:xxx)
    // 对应 sys_menu: parent=系统管理(id=1) -> 用户管理(id=2) -> 按钮
    // ==========================================
    /** 用户查询 */
    public static final String USER_QUERY = "sys:user:query";
    /** 用户新增 */
    public static final String USER_ADD = "sys:user:add";
    /** 用户修改 */
    public static final String USER_UPDATE = "sys:user:update";
    /** 用户删除 */
    public static final String USER_DELETE = "sys:user:delete";

    // ==========================================
    // 系统管理 -> 角色管理 (sys:role:xxx)
    // 预留，新增 sys_menu 数据后直接复用
    // ==========================================
    /** 角色查询 */
    public static final String ROLE_QUERY = "sys:role:query";
    /** 角色新增 */
    public static final String ROLE_ADD = "sys:role:add";
    /** 角色修改 */
    public static final String ROLE_UPDATE = "sys:role:update";
    /** 角色删除 */
    public static final String ROLE_DELETE = "sys:role:delete";
    /** 角色分配菜单 */
    public static final String ROLE_ASSIGN_MENU = "sys:role:assign:menu";
    /** 角色分配用户 */
    public static final String ROLE_ASSIGN_USER = "sys:role:assign:user";

    // ==========================================
    // 系统管理 -> 菜单管理 (sys:menu:xxx)
    // 预留
    // ==========================================
    /** 菜单查询 */
    public static final String MENU_QUERY = "sys:menu:query";
    /** 菜单新增 */
    public static final String MENU_ADD = "sys:menu:add";
    /** 菜单修改 */
    public static final String MENU_UPDATE = "sys:menu:update";
    /** 菜单删除 */
    public static final String MENU_DELETE = "sys:menu:delete";

    // ==========================================
    // 系统管理 -> 系统配置/日志 (sys:config:xxx / sys:log:xxx)
    // 预留
    // ==========================================
    /** 系统配置查询 */
    public static final String SYS_CONFIG_QUERY = "sys:config:query";
    /** 系统配置修改 */
    public static final String SYS_CONFIG_UPDATE = "sys:config:update";
    /** 系统日志查询 */
    public static final String SYS_LOG_QUERY = "sys:log:query";

    // ==========================================
    // 公告管理 (sys:notice:xxx)
    // 对应 sys_menu: 公告管理菜单 -> 按钮
    // ==========================================
    /** 公告查询 */
    public static final String NOTICE_QUERY = "sys:notice:query";
    /** 公告新增 */
    public static final String NOTICE_ADD = "sys:notice:add";
    /** 公告修改 */
    public static final String NOTICE_UPDATE = "sys:notice:update";
    /** 公告删除 */
    public static final String NOTICE_DELETE = "sys:notice:delete";
    /** 公告阅读日志查询 */
    public static final String NOTICE_LOG_QUERY = "sys:notice:log";

    // ==========================================
    // 轮播图管理 (sys:banner:xxx)
    // 对应 sys_menu: 轮播图管理菜单 -> 按钮
    // ==========================================
    /** 轮播图查询 */
    public static final String BANNER_QUERY = "sys:banner:query";
    /** 轮播图新增 */
    public static final String BANNER_ADD = "sys:banner:add";
    /** 轮播图修改 */
    public static final String BANNER_UPDATE = "sys:banner:update";
    /** 轮播图删除 */
    public static final String BANNER_DELETE = "sys:banner:delete";

    // ==========================================
    // 文件管理 (file:xxx:xxx)
    // 预留
    // ==========================================
    /** 文件上传 */
    public static final String FILE_UPLOAD = "file:upload:save";
    /** 文件下载 */
    public static final String FILE_DOWNLOAD = "file:download:get";
    /** 文件删除 */
    public static final String FILE_DELETE = "file:upload:delete";
}
