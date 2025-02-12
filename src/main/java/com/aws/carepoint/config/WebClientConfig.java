package com.aws.carepoint.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig { // OpenAI API 호출을 위한 설정 클래스
    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }
}
