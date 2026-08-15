package com.atguigu.meet.model.entity.banner;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 轮播图实体
 */
@Data
@TableName("banner")
public class Banner extends Model<Banner> {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 轮播图地址 */
    private String imgUrl;

    /** 轮播位置：home=首页 seckill=抢购 */
    private String position;

    /** 权重，越大越靠前 */
    private Integer sort = 0;

    /** 跳转url */
    private String linkValue;

    /** 状态：0-禁用，1-启用 */
    private Integer status = 1;

    /** 逻辑删除 0未删 1已删 */
    @JsonIgnore
    @TableLogic
    private Integer isDeleted = 0;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    /** 操作人ID(管理员id) */
    private Long createBy;

    /** 更新人ID */
    private Long updateBy;
}
