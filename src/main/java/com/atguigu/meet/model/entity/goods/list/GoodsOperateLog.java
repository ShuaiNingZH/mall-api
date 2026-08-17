package com.atguigu.meet.model.entity.goods.list;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 商品操作日志实体
 * operate_type: 1新增 2编辑 3删除 4上下架
 */
@Data
@TableName("t_goods_operate_log")
public class GoodsOperateLog extends Model<GoodsOperateLog> {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 商品ID */
    private Long goodsId;

    /** 操作管理员ID */
    private Long adminId;

    /** 操作类型 1新增 2编辑 3删除 4上下架 */
    private Integer operateType;

    /** 变更内容JSON */
    private String content;

    private LocalDateTime createTime;
}
