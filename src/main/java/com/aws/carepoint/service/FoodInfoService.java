//package com.aws.carepoint.service;
//
//import com.aws.carepoint.entity.FoodInfo;
//import com.aws.carepoint.repository.FoodInfoRepository;
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//
//import java.net.URLEncoder;
//import java.nio.charset.StandardCharsets;
//import java.util.ArrayList;
//import java.util.List;
//
//@Service
//public class FoodInfoService {
//
//    private final FoodInfoRepository foodInfoRepository;
//    private final RestTemplate restTemplate = new RestTemplate();
//
//    @Value("${api.key}") // application.yml에서 API Key 가져오기
//    private String apiKey;
//
//    @Value("${api.url}") // application.yml에서 API URL 가져오기
//    private String apiUrl;
//
//    public FoodInfoService(FoodInfoRepository foodInfoRepository) {
//        this.foodInfoRepository = foodInfoRepository;
//    }
//
//    // API 호출 후 데이터 저장
//    public void fetchAndSaveFoodData(String menu) {
//        try {
//            // 음식명을 URL 인코딩 (한글 깨짐 방지)
//            String encodedFoodName = URLEncoder.encode(menu, StandardCharsets.UTF_8);
//
//            // 최종 API 요청 URL 생성 (API Key 포함)
//            String requestUrl = apiUrl + "?serviceKey=" + apiKey +
//                    "&FOOD_NM_KR=" + encodedFoodName +
//                    "&numOfRows=15&pageNo=1&type=json";
//
//            // API 요청 및 JSON 응답 받기
//            String jsonResponse = restTemplate.getForObject(requestUrl, String.class);
//
//            // JSON 파싱
//            ObjectMapper objectMapper = new ObjectMapper();
//            JsonNode rootNode = objectMapper.readTree(jsonResponse);
//
//            List<FoodInfo> foodList = new ArrayList<>();
//
//            for (JsonNode item : rootNode.path("body").path("items")) {  // "body.items" 배열 안의 데이터 파싱
//                FoodInfo food = new FoodInfo();
//                food.setMenu(item.path("FOOD_NM_KR").asText()); // 메뉴 이름
//                food.setKcal(item.path("AMT_NUM1").asInt()); // 칼로리
//                food.setCarbohydrate((float) item.path("AMT_NUM7").asDouble()); // 탄수화물
//                food.setProtein((float) item.path("AMT_NUM3").asDouble()); // 단백질
//                food.setFat((float) item.path("AMT_NUM4").asDouble()); // 지방
//
//                foodList.add(food);
//            }
//
//            // DB 저장
//            foodInfoRepository.saveAll(foodList);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    // 음식명 검색
//    public List<FoodInfo> searchFoodByName(String menu) {
//        return foodInfoRepository.findByMenuContaining(menu);
//    }
//
//
//}
