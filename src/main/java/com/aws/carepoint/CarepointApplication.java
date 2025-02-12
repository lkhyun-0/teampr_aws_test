package com.aws.carepoint;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

@MapperScan("com.aws.carepoint.mapper") // 🔹 패키지 경로에 맞게 설정
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
@EnableScheduling       // 스케쥴링 기능활성화
public class CarepointApplication {

    public static void main(String[] args) {
        SpringApplication.run(CarepointApplication.class, args);
    }

}
