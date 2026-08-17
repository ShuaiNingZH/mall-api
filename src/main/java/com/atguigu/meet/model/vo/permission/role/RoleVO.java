package com.atguigu.meet.model.vo.permission.role;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 角色响应VO
 */
@Data
public class RoleVO {
    private Long id;
    private String roleName;
    private String roleCode;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    /** 角色拥有的菜单ID列表 */
    private List<Long> menuIds;
}