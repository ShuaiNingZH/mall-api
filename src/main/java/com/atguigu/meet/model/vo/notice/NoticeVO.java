package com.atguigu.meet.model.vo.notice;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 公告响应VO
 */
@Data
public class NoticeVO {
    private Long id;
    private String title;
    private String content;
    private Integer sort;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Long createBy;
    private Long updateBy;

    /** 阅读次数（详情接口聚合返回） */
    private Long readCount;
}
