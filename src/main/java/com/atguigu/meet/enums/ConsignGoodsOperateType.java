package com.atguigu.meet.enums;

/**
 * 抢购托售商品操作日志类型枚举（对应 t_consign_goods_operate_log.operate_type）
 * <p>
 * SHELF_ON / SHELF_OFF 共享 code=4，区别仅在中文描述（上架/下架）。
 * BIZ_FLOW 的 defaultDesc 是静态的，动态详情由 buildBizFlowDesc(from, to) 拼装。
 */
public enum ConsignGoodsOperateType {

    ADD(1, "新增托售商品"),
    EDIT(2, "编辑托售商品"),
    DELETE(3, "删除托售商品"),
    SHELF_ON(4, "托售商品上架"),
    SHELF_OFF(4, "托售商品下架"),
    BIZ_FLOW(5, "业务状态流转");

    private final int code;
    private final String defaultDesc;

    ConsignGoodsOperateType(int code, String defaultDesc) {
        this.code = code;
        this.defaultDesc = defaultDesc;
    }

    public int getCode() {
        return code;
    }

    /** 默认操作中文描述，列表展示用 */
    public String getDefaultDesc() {
        return defaultDesc;
    }

    /**
     * 构造业务状态流转动态描述
     * <p>
     * 示例：状态流转:挂卖中->已抢购待付款
     *
     * @param fromName 流转前状态中文名
     * @param toName   流转后状态中文名
     */
    public String buildBizFlowDesc(String fromName, String toName) {
        return "状态流转:" + fromName + "->" + toName;
    }
}
