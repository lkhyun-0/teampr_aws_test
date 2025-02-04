package com.aws.carepoint.service;

import com.aws.carepoint.dto.FoodDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class FoodService {

    // 여기에 API 주소와 API 키를 직접 입력
    private final String API_URL = "https://apis.data.go.kr/1471000/FoodNtrCpntDbInfo01/getFoodNtrCpntDbInq01";
    private final String API_KEY = "w0x%2F%2FhcjF7XzsNtQz1z%2BN7HBII%2B43N4Jty2e4NO32EsUKlHqoeqj1HwLAde%2BHaZRphi3YMnhCU4fKeYzIvs8uA%3D%3D";

    public List<FoodDTO> searchFood(String query) {
        RestTemplate restTemplate = new RestTemplate();
        ObjectMapper objectMapper = new ObjectMapper();
        List<FoodDTO> foodList = new ArrayList<>();

        try {
            // 음식명 URL 인코딩
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);

            // API 요청 URL 생성 (API_KEY 인코딩 X)
            String url = API_URL + "?serviceKey=" + API_KEY
                    + "&FOOD_NM_KR=" + encodedQuery
                    + "&numOfRows=5"
                    + "&pageNo=1"
                    + "&type=json";

            // 요청 URL 확인
            System.out.println("✅ API 요청 URL: " + url);

            // API 호출 (🔥 UTF-8 인코딩 강제 적용)
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));  // 나는 JSON을 원해
            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            String responseBody = responseEntity.getBody();

            // 응답 로그 확인
            System.out.println("✅ API 응답: " + responseBody);

            // JSON 파싱
            JsonNode root = objectMapper.readTree(responseBody);

            // 응답 데이터 구조 확인 후 `body.items` 존재 여부 체크
            if (root.has("body") && root.get("body").has("items")) {
                JsonNode items = root.get("body").get("items");

                for (JsonNode item : items) {
                    FoodDTO foodDto = new FoodDTO();
                    foodDto.setMenu(item.path("FOOD_NM_KR").asText());
                    foodDto.setKcal((int) item.path("AMT_NUM1").asDouble());
                    foodDto.setProtein((float) item.path("AMT_NUM3").asDouble());
                    foodDto.setFat((float) item.path("AMT_NUM4").asDouble());
                    foodDto.setCarbohydrate((float) item.path("AMT_NUM7").asDouble());
                    foodList.add(foodDto);
                }
            } else {
                System.out.println("❌ API 응답에 'body.items' 없음.");
            }
        } catch (Exception e) {
            System.out.println("❌ API 요청 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }

        return foodList;
    }
}


