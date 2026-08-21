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
 * content JSON 约定: {"before":{...},"after":{...},"changedFields":["xxx"],"remark":"编辑商品基础信息"}
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

    /** 操作中文描述(新增商品/编辑商品/删除商品/上架/下架)，列表展示用，避免每次解析 JSON */
    private String operateDesc;

    /** 操作人客户端 IP，溯源定位操作来源 */
    private String ip;

    /** 操作人浏览器/客户端设备信息，安全审计用 */
    private String userAgent;

    /**
     * 变更内容 JSON
     * 格式: {"before":{...},"after":{...},"changedFields":["xxx"],"remark":"..."}
     * before/after 为前后快照，changedFields 为本次修改字段名集合，remark 为备注
     */
    private String content;

    private LocalDateTime createTime;
}
