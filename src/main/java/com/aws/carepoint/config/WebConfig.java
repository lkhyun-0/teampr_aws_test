package com.aws.carepoint.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**") // URL 접근 경로
                .addResourceLocations("file:uploads/") // 실제 저장되는 로컬 경로
                .setCachePeriod(3600); // 캐싱(초 단위) - 선택사항
    }
}
