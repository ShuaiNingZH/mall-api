package com.atguigu.meet.config;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @Description
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

        return Jackson2ObjectMapperBuilder.json()
                .modules(javaTimeModule)
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }
}
