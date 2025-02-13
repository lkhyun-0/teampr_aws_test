package com.aws.carepoint.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity  // 🔹 Spring Security 활성화
public class SecurityConfig {
    @Bean   // 🔹 비밀번호 암호화 기능
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // ✅ CSRF 비활성화 (테스트용) todo 나중에 필요한 부분들 페이지 추가해야함
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll() // ✅ 모든 요청 허용 (테스트용)
                )
                .formLogin(login -> login
                        .loginPage("/user/signIn")
                        .permitAll()
                ) // ✅ 세미콜론 제거하고 바로 이어서 설정
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/user/signIn") // ✅ 카카오 로그인도 동일한 로그인 페이지 사용
                        .defaultSuccessUrl("/") // ✅ 로그인 성공 시 홈으로 이동
                );
        return http.build();
    }


}


