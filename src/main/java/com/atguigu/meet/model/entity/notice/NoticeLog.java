package com.atguigu.meet.model.entity.notice;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 公告阅读日志实体
 * 记录用户阅读公告的行为：谁读了哪条公告、什么时候读的
 */
@Data
@TableName("t_notice_log")
public class NoticeLog extends Model<NoticeLog> {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 公告ID(t_notice.id) */
    private Long noticeId;

    /** 用户ID(sys_user.id) */
    private Long userId;

    /** 阅读时间 */
    private LocalDateTime readTime;

    private LocalDateTime createTime;
}
