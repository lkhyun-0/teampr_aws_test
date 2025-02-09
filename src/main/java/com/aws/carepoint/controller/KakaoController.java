package com.aws.carepoint.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;


import java.util.Map;

@Controller
@RequestMapping("/user/kakao")
public class KakaoController {

    // ✅ application-DB.yml에서 설정된 값 불러오기
    @Value("${kakao.client-id}")
    private String kakaoClientId;

    @Value("${kakao.redirect-uri}")
    private String kakaoRedirectUri;

    @Value("${kakao.token-url}")
    private String kakaoTokenUrl;

    @Value("${kakao.user-info-url}")
    private String kakaoUserInfoUrl;

    @GetMapping("/callback")
    public ModelAndView kakaoCallback(@RequestParam String code, HttpSession session) {
        // 1️⃣ 카카오 인증 코드로 액세스 토큰 요청
        String accessToken = getAccessToken(code);
        if (accessToken == null) {  // 토큰없으면
            return new ModelAndView("redirect:/login?error=카카오 로그인 실패");
        }

        // 2️⃣ 액세스 토큰으로 사용자 정보 요청
        Map<String, Object> userInfo = getUserInfo(accessToken);
        if (userInfo == null) { // 사용자 정보 못받아오면
            return new ModelAndView("redirect:/login?error=사용자 정보 가져오기 실패");
        }

        // 3️⃣ 사용자 정보 확인 및 회원가입/로그인 처리
        String email = (String) userInfo.get("email");
        String nickname = (String) userInfo.get("nickname");

        // (여기서 DB에 이메일이 있는지 확인하고, 없으면 회원가입)
        if (session.getAttribute("user") == null) {
            session.setAttribute("user", email); // 세션에 이메일 저장 (로그인)
        }

        return new ModelAndView("redirect:/user/mainPage"); // 로그인 후 메인페이지로 이동
    }

    // ✅ 1️⃣ 카카오 인증 코드로 액세스 토큰 요청
    private String getAccessToken(String code) {
        RestTemplate restTemplate = new RestTemplate();

        String requestBody = "grant_type=authorization_code&client_id=" + kakaoClientId +
                "&redirect_uri=" + kakaoRedirectUri + "&code=" + code;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);
        ResponseEntity<Map> response = restTemplate.exchange(kakaoTokenUrl, HttpMethod.POST, request, Map.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            Map<String, Object> responseBody = response.getBody();
            return (String) responseBody.get("access_token");
        }
        return null;
    }

    // ✅ 2️⃣ 액세스 토큰으로 사용자 정보 요청
    private Map<String, Object> getUserInfo(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(kakaoUserInfoUrl, HttpMethod.GET, request, Map.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            Map<String, Object> userInfo = response.getBody();
            Map<String, Object> kakaoAccount = (Map<String, Object>) userInfo.get("kakao_account");
            String email = (String) kakaoAccount.get("email");
            String nickname = (String) ((Map<String, Object>) userInfo.get("properties")).get("nickname");

            return Map.of("email", email, "nickname", nickname);
        }
        return null;
    }


    @GetMapping("/logout")
    public ModelAndView kakaoLogout(HttpSession session) {
        String accessToken = (String) session.getAttribute("accessToken");

        if (accessToken != null) {
            String logoutUrl = "https://kapi.kakao.com/v1/user/logout";

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);

            HttpEntity<Void> request = new HttpEntity<>(headers);
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.exchange(logoutUrl, HttpMethod.POST, request, String.class);

            System.out.println("카카오 로그아웃 요청 결과: " + response.getBody());
        }

        // 세션 초기화
        session.invalidate();

        return new ModelAndView("redirect:/mainPage"); // 로그아웃 후 로그인 페이지로 이동
    }

}
