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

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor())
                .addPathPatterns("/**") // 모든 요청에 대해 인터셉터 적용
                .excludePathPatterns(
                        "/user/signIn",  // 로그인 페이지는 인터셉터 제외
                        "/user/doSignIn", // 로그인 처리 API 제외
                        "/user/signUp",  // 회원가입 페이지 제외
                        "/user/dosignUp", // 회원가입 처리 제외
                        "/user/logout",  // 로그아웃 처리 제외
                        "/user/mainPage",  // 메인페이지 처리 제외
                        "/plan/hospital", // 병원찾기 제외
                        "/notice/noticeList", // 공지사항 목록 제외
                        "/css/**", "/js/**", "/images/**" // 정적 리소스 제외
                );
    }
}
