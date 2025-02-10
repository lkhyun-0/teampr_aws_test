package com.aws.carepoint.controller;

import com.aws.carepoint.dto.UsersDto;
import com.aws.carepoint.service.KakaoAuthService;
import com.aws.carepoint.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class KakaoAuthController {

    private final KakaoAuthService kakaoAuthService;
    private final UserService userService;

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String redirectUri;

    public KakaoAuthController(KakaoAuthService kakaoAuthService, UserService userService) {
        this.kakaoAuthService = kakaoAuthService;
        this.userService = userService;
    }

    @GetMapping("/login/kakao/auth-url")
    public ResponseEntity<Map<String, String>> getKakaoAuthUrl() {
        String kakaoUrl = "https://kauth.kakao.com/oauth/authorize" +
                "?client_id=" + clientId +
                "&redirect_uri=" + redirectUri +
                "&response_type=code";

        Map<String, String> response = new HashMap<>();
        response.put("kakaoAuthUrl", kakaoUrl);

        return ResponseEntity.ok(response); // ✅ JSON 형식으로 반환
    }



}
