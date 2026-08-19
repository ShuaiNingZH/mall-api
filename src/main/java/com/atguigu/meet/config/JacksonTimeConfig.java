package com.atguigu.meet.config;

import com.atguigu.meet.config.jackson.LenientBooleanDeserializer;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Jackson 全局配置
 * <ul>
 *   <li>时间格式：yyyy-MM-dd HH:mm:ss</li>
 *   <li>Boolean 宽松反序列化：支持 0/1、true/false、"0"/"1"</li>
 * </ul>
 *
 * @Date 2026-06-01 14:55
 */
@Configuration
public class JacksonTimeConfig {
    @Bean
    public Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder() {
        JavaTimeModule javaTimeModule = new JavaTimeModule();

        // 1. 配置 LocalDateTime 格式: yyyy-MM-dd HH:mm:ss
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(dateTimeFormatter));
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(dateTimeFormatter));

        // 2. 配置 LocalDate 格式: yyyy-MM-dd
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(dateFormatter));
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(dateFormatter));

        // 3. Boolean 宽松反序列化：DTO/VO 的 Boolean 字段兼容接收 0/1/true/false/"0"/"1"
        SimpleModule booleanModule = new SimpleModule("LenientBooleanModule");
        LenientBooleanDeserializer boolDeser = new LenientBooleanDeserializer();
        booleanModule.addDeserializer(Boolean.class, boolDeser);
        booleanModule.addDeserializer(boolean.class, boolDeser);

        return Jackson2ObjectMapperBuilder.json()
                .modules(javaTimeModule, booleanModule)
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }
}
