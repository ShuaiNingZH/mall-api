package com.atguigu.meet.model.vo.user;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户响应VO
 */
@Data
public class UserVO {
    private Long id;

    private String username;

    private String nickname;

    private String email;

    private String gender;

    private Integer age;

    private String avatar;

    private LocalDate birthday;

    private String phone;

    private String status;

    /** 邀请人ID */
    private Long inviterId;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}