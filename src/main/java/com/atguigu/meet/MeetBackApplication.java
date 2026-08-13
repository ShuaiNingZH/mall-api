package com.atguigu.meet;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@MapperScan(basePackages = "com.atguigu.meet.mapper")
@EnableTransactionManagement
public class MeetBackApplication {

    public static void main(String[] args) {
        /*// 1. 生成 32 字节安全随机数（HS256 标准要求）
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        // 2. 直接转 Base64 → 自动变成 44 位，结尾带 =
        String base64key = Base64.getEncoder().encodeToString(bytes);
        System.out.println(base64key);*/
        SpringApplication.run(MeetBackApplication.class, args);
    }

}
