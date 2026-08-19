package com.atguigu.meet.config.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

/**
 * 宽松的 Boolean 反序列化器
 * <p>
 * 支持以下输入 → Boolean:
 * <ul>
 *   <li>Boolean: true / false</li>
 *   <li>Number:  1 → true, 0 → false</li>
 *   <li>String:  "1"/"true"/"TRUE" → true, "0"/"false"/"FALSE" → false</li>
 * </ul>
 * 用于 DTO/VO 的 Boolean 字段，兼容前端传 0/1 或 true/false。
 */
public class LenientBooleanDeserializer extends JsonDeserializer<Boolean> {

    @Override
    public Boolean deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (p == null) {
            return null;
        }
        JsonToken t = p.currentToken();
        if (t == null) {
            return null;
        }
        switch (t) {
            case VALUE_TRUE:
                return Boolean.TRUE;
            case VALUE_FALSE:
                return Boolean.FALSE;
            case VALUE_STRING:
                String text = p.getText();
                if (text == null) return null;
                String s = text.trim();
                if ("1".equals(s) || "true".equalsIgnoreCase(s)) return Boolean.TRUE;
                if ("0".equals(s) || "false".equalsIgnoreCase(s)) return Boolean.FALSE;
                return null;
            case VALUE_NUMBER_INT:
                int i = p.getIntValue();
                return i == 1 ? Boolean.TRUE : (i == 0 ? Boolean.FALSE : null);
            case VALUE_NULL:
                return null;
            default:
                return null;
        }
    }
}
