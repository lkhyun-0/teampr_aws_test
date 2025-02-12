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

    @Value("${kakao.client-id}")  // ✅ 경로 수정!
    private String clientId;

    @Value("${kakao.redirect-uri}")  // ✅ 경로 수정!
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

    @GetMapping("/login/oauth2/code/kakao")
    public ResponseEntity<Map<String, Object>> kakaoCallback(@RequestParam("code") String code, HttpSession session) {
        //System.out.println("📢 받은 카카오 인증 코드: " + code);

        // 1️⃣ 액세스 토큰 요청
        String accessToken = kakaoAuthService.getKakaoAccessToken(code);
        //System.out.println("📢 받은 액세스 토큰: " + accessToken);

        // 2️⃣ 사용자 정보 요청
        Map<String, Object> userInfo = kakaoAuthService.getUserInfo(accessToken);
        //System.out.println("📢 받은 사용자 정보: " + userInfo);

        // 3️⃣ 사용자 정보 저장 및 로그인 처리
        UsersDto usersDto = userService.processKakaoLogin(userInfo, session);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "카카오 로그인 성공!");
        response.put("redirect", "/user/mainPage"); // 🚀 메인 페이지로 리다이렉트

        return ResponseEntity.ok(response);
    }

}
