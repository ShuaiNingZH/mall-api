package com.atguigu.meet.enums;

/**
 * 商品操作日志类型枚举（对应 t_goods_operate_log.operate_type）
 * <p>
 * SHELF_ON / SHELF_OFF 共享 code=4，区别仅在中文描述（上架/下架），
 * 调用方按 dto.status 真值选择枚举值即可，无需再硬编码字符串。
 */
public enum GoodsOperateType {

    ADD(1, "新增商品"),
    EDIT(2, "编辑商品"),
    DELETE(3, "删除商品"),
    SHELF_ON(4, "商品上架"),
    SHELF_OFF(4, "商品下架");

    private final int code;
    private final String defaultDesc;

    GoodsOperateType(int code, String defaultDesc) {
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
}
