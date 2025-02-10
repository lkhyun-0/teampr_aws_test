package com.aws.carepoint.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class KakaoAuthService {

    @Value("${kakao.client-id}")
    private String kakaoClientId;

    @Value("${kakao.client-secret}")
    private String kakaoClientSecret;

    @Value("${kakao.redirect-uri}")
    private String kakaoRedirectUri;

    private final RestTemplate restTemplate = new RestTemplate();


    // 1️⃣ 카카오로부터 액세스 토큰 받기
    public String getKakaoAccessToken(String code) {
        System.out.println("📢 로드된 kakao.client-secret 값: " + kakaoClientSecret);

        String tokenUrl = "https://kauth.kakao.com/oauth/token";

        // ✅ 1. HTTP 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // ✅ 2. 요청 본문 (application/x-www-form-urlencoded)
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", kakaoClientId);
        body.add("redirect_uri", kakaoRedirectUri);
        body.add("code", code);
        body.add("client_secret", kakaoClientSecret); // 보안을 위해 Secret Key 추가

        // ✅ 3. HTTP 요청 객체 생성
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        // ✅ 4. RestTemplate을 사용하여 POST 요청 전송
        ResponseEntity<String> response = restTemplate.exchange(
                tokenUrl,
                HttpMethod.POST,
                request,
                String.class
        );

        try {
            // ✅ 5. JSON 응답 파싱
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response.getBody());

            String accessToken = jsonNode.get("access_token").asText();
            System.out.println("📢 받은 액세스 토큰: " + accessToken);

            return accessToken;
        } catch (Exception e) {
            throw new RuntimeException("카카오 액세스 토큰 요청 실패", e);
        }
    }







    public Map<String, Object> getUserInfo(String accessToken) {
        String userInfoUrl = "https://kapi.kakao.com/v2/user/me";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken); // Bearer 인증 방식 사용
        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                userInfoUrl, HttpMethod.GET, entity, String.class
        );

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response.getBody());

            // ✅ 1. 카카오에서 제공하는 사용자 정보 추출
            String kakaoId = jsonNode.get("id").asText(); // 카카오 고유 ID
            String email = jsonNode.get("kakao_account").get("email").asText(); // 이메일
            String nickname = jsonNode.get("properties").get("nickname").asText(); // 닉네임
            String name = jsonNode.get("kakao_account").get("profile").get("nickname").asText(); // 이름 (nickname과 같을 수 있음)
            String phone = jsonNode.get("kakao_account").get("phone_number").asText(); // 전화번호

            // ✅ 2. 랜덤 비밀번호 생성 (카카오 로그인용)
            String randomPwd = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 12); // 12자리 랜덤 비밀번호

            // ✅ 3. 사용자 정보를 Map에 저장
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", kakaoId);
            userInfo.put("email", email);
            userInfo.put("nickname", nickname);
            userInfo.put("name", name);
            userInfo.put("phone", phone);
            userInfo.put("password", randomPwd); // 랜덤 비밀번호 추가

            return userInfo;
        } catch (Exception e) {
            throw new RuntimeException("카카오 유저 정보 요청 실패", e);
        }
    }
}
