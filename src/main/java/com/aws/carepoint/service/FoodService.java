package com.aws.carepoint.service;

import com.aws.carepoint.domain.Food;
import com.aws.carepoint.domain.FoodList;
import com.aws.carepoint.dto.FoodDto;
import com.aws.carepoint.dto.FoodRecordRequest;
import com.aws.carepoint.mapper.FoodMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FoodService {

    private final FoodMapper foodMapper;

    private final String API_URL = "https://apis.data.go.kr/1471000/FoodNtrCpntDbInfo01/getFoodNtrCpntDbInq01";
    private final String API_KEY = "w0x%2F%2FhcjF7XzsNtQz1z%2BN7HBII%2B43N4Jty2e4NO32EsUKlHqoeqj1HwLAde%2BHaZRphi3YMnhCU4fKeYzIvs8uA%3D%3D";

    public List<FoodDto> searchFood(String query) {
        RestTemplate restTemplate = new RestTemplate();
        ObjectMapper objectMapper = new ObjectMapper();
        List<FoodDto> foodList = new ArrayList<>();

        try {
            // 음식명 URL 인코딩
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);

            // URI 객체 생성 (URI를 사용하여 자동 인코딩 처리)
            URI uri = new URI(API_URL +
                    "?serviceKey=" + API_KEY +
                    "&FOOD_NM_KR=" + encodedQuery +
                    "&numOfRows=30" +
                    "&pageNo=1" +
                    "&type=json");

            // 요청 URI 확인
            System.out.println("API 요청 URI: " + uri);

            // API 호출 (getForObject 사용)
            String responseBody = restTemplate.getForObject(uri, String.class);

            // 응답 로그 확인
            System.out.println("API 응답: " + responseBody);

            // JSON 파싱
            JsonNode root = objectMapper.readTree(responseBody);

            // 응답 데이터 구조 확인 후 `body.items` 존재 여부 체크
            if (root.has("body") && root.get("body").has("items")) {
                JsonNode items = root.get("body").get("items");

                for (JsonNode item : items) {
                    FoodDto foodDto = new FoodDto();
                    foodDto.setMenu(item.path("FOOD_NM_KR").asText());
                    foodDto.setKcal((int) item.path("AMT_NUM1").asDouble());
                    foodDto.setProtein((float) item.path("AMT_NUM3").asDouble());
                    foodDto.setFat((float) item.path("AMT_NUM4").asDouble());
                    foodDto.setCarbohydrate((float) item.path("AMT_NUM7").asDouble());
                    foodList.add(foodDto);
                }
            } else {
                System.out.println("API 응답에 'body.items' 없음.");
            }
        } catch (Exception e) {
            System.out.println("API 요청 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }

        return foodList;
    }


    @Transactional
    public void recordFood(FoodRecordRequest request) {
        // 1️⃣ `food` 테이블에 식단 기록 추가
        Food food = new Food();
        food.setSelectDate(LocalDate.parse(request.getSelectDate()));
        food.setFoodType(request.getFoodType());
        food.setUserPk(request.getUserPk());
        foodMapper.insertFood(food); // `food_pk` 자동 생성됨

        // 2️⃣ `foodlist` 테이블에 개별 음식 저장
        for (var foodDto : request.getFoodList()) {
            FoodList foodList = new FoodList();
            foodList.setMenu(foodDto.getMenu());
            foodList.setKcal(foodDto.getKcal());
            foodList.setProtein(foodDto.getProtein());
            foodList.setCarbohydrate(foodDto.getCarbohydrate());
            foodList.setFat(foodDto.getFat());
            foodList.setFoodPk(food.getFoodPk()); // 외래키 설정
            foodMapper.insertFoodList(foodList);
        }
    }


    //  특정 날짜의 식단 조회
    public List<FoodList> getFoodByDate(int userPk, String selectDate) {
        return foodMapper.getFoodByDate(userPk, selectDate);
    }

    // 개별 음식 삭제
    public void deleteFood(int foodListPk) {
        foodMapper.deleteFood(foodListPk);
    }




}
