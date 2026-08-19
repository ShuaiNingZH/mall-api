package com.atguigu.meet.config.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * String "0"/"1" → Boolean false/true 序列化器
 * <p>
 * 用于 Entity 类中的 String 状态字段（如 status="1" 启用 / status="0" 禁用），
 * 在 JSON 响应时输出 true/false 而非 "1"/"0"。
 * <p>
 * 用法：字段上标注 @JsonSerialize(using = String01ToBooleanSerializer.class)
 */
public class String01ToBooleanSerializer extends JsonSerializer<String> {

    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value == null) {
            gen.writeNull();
        } else if ("1".equals(value)) {
            gen.writeBoolean(true);
        } else if ("0".equals(value)) {
            gen.writeBoolean(false);
        } else {
            // 非 "0"/"1" 的字符串原样输出，避免误转换（如 gender="0"/"1"/"2" 场景下 gender 字段不会加此注解）
            gen.writeString(value);
        }
    }
}
