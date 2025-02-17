package com.aws.carepoint.config;

import com.aws.carepoint.interceptor.LoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**") // URL 접근 경로
                .addResourceLocations("file:uploads/") // 실제 저장되는 로컬 경로
                .addResourceLocations("file:D:/team_Pr/workspace/uploads") // 실제 저장되는 로컬 경로
                .setCachePeriod(3600); // 캐싱(초 단위) - 선택사항

    }


    // 로그인 인터셉터 설정
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor())
                .addPathPatterns(
                        "/food/analysis", // 식단분석
                        "/food/foodRecord", // 식단기록
                        "/food/foodList", // 식단 목록
                        "/plan/plan"

                ); // 필요한 경로만 인터셉터 적용 하면 됩니당
    }
}
