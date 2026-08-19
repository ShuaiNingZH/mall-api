package com.atguigu.meet.config.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * Integer 0/1 → Boolean true/false 序列化器
 * <p>
 * 用于 Entity 类中的 Integer 状态字段（如 status=1 启用 / status=0 禁用），
 * 在 JSON 响应时输出 true/false 而非 1/0。
 * <p>
 * 用法：字段上标注 @JsonSerialize(using = Integer01ToBooleanSerializer.class)
 */
public class Integer01ToBooleanSerializer extends JsonSerializer<Integer> {

    @Override
    public void serialize(Integer value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value == null) {
            gen.writeNull();
        } else if (value == 1) {
            gen.writeBoolean(true);
        } else if (value == 0) {
            gen.writeBoolean(false);
        } else {
            // 非 0/1 的 Integer 值原样输出数字，避免误转换（如 type=0/1/2）
            gen.writeNumber(value);
        }
    }
}
