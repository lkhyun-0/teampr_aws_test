package com.aws.carepoint.service;

import com.aws.carepoint.domain.Food;
import com.aws.carepoint.domain.FoodList;
import com.aws.carepoint.dto.*;
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
                    "&numOfRows=50" +
                    "&pageNo=1" +
                    "&type=json");

            // 요청 URI 확인
            //System.out.println("API 요청 URI: " + uri);

            // API 호출 (getForObject 사용)
            String responseBody = restTemplate.getForObject(uri, String.class);

            // 응답 로그 확인
            //System.out.println("API 응답: " + responseBody);

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
                    foodDto.setServingSize((float) item.path("Z10500").asDouble()); // 1인분 기준양
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
        // `food` 테이블에 식단 기록 추가
        Food food = new Food();
        food.setSelectDate(LocalDate.parse(request.getSelectDate()));
        food.setFoodType(request.getFoodType());
        food.setUserPk(request.getUserPk());
        foodMapper.insertFood(food); // `food_pk` 자동 생성됨

        // `foodlist` 테이블에 개별 음식 저장
        for (var foodDto : request.getFoodList()) {
            FoodList foodList = new FoodList();
            foodList.setMenu(foodDto.getMenu());
            foodList.setKcal(foodDto.getKcal());
            foodList.setProtein(foodDto.getProtein());
            foodList.setCarbohydrate(foodDto.getCarbohydrate());
            foodList.setFat(foodDto.getFat());
            foodList.setAmount(foodDto.getAmount()); // 그람수
            foodList.setFoodPk(food.getFoodPk()); // 외래키 설정
            foodMapper.insertFoodList(foodList);
        }
    }


    //  특정 날짜의 식단 조회
    public List<FoodList> getFoodByDate(int userPk, String selectDate) {
        return foodMapper.getFoodByDate(userPk, selectDate);
    }

    @Transactional
    public void deleteFood(int foodListPk, String selectDate, String foodType, int userPk) {
        // 삭제할 foodListPk의 food_pk 가져오기
        Integer foodPk = foodMapper.getFoodPkByFoodListPk(foodListPk);
        if (foodPk == null) {
            return; // foodListPk가 존재하지 않으면 바로 종료
        }

        // foodlist에서 개별 음식 삭제
        foodMapper.deleteFood(foodListPk);

        // 해당 food_pk의 남은 음식 개수 확인
        int remainingCount = foodMapper.countFoodListByFoodPk(foodPk);

        // 만약 해당 food_pk에 남은 음식이 없다면 food 테이블에서도 삭제
        if (remainingCount == 0) {
            foodMapper.deleteEmptyFood(foodPk);
        }
    }



    @Transactional
    public void updateMeal(UpdateMealRequest request) {
        // 기존 식단 기록 조회
        List<FoodList> existingFoods = foodMapper.getFoodByDateAndType(
                request.getUserPk(), request.getSelectDate(), request.getFoodType()
        );

        // 클라이언트가 보낸 수정된 음식 리스트
        List<FoodDto> newFoodList = request.getFoodList();

        // 기존 식단이 없으면 새로운 food_pk 생성 (즉, 새로운 식사 기록 추가)
        Integer foodPk;
        if (existingFoods.isEmpty()) {
            // 기존 기록이 없는 경우, 새로운 식단 기록 추가 (아침/점심/저녁 첫 기록)
            Food newMeal = new Food();
            newMeal.setSelectDate(LocalDate.parse(request.getSelectDate()));
            newMeal.setFoodType(request.getFoodType());
            newMeal.setUserPk(request.getUserPk());
            foodMapper.insertFood(newMeal); // 새로운 food_pk 생성
            foodPk = newMeal.getFoodPk();
        } else {
            // 기존 기록이 있으면 기존 foodPk 사용
            foodPk = existingFoods.get(0).getFoodPk();
        }

        for (int i = 0; i < newFoodList.size(); i++) {
            FoodDto foodDto = newFoodList.get(i);

            if (foodDto.getFoodListPk() != null) {
                // 🛠 기존 음식이면 UPDATE
                FoodList updatedFood = new FoodList();
                updatedFood.setFoodListPk(foodDto.getFoodListPk());
                updatedFood.setMenu(foodDto.getMenu());
                updatedFood.setKcal(foodDto.getKcal());
                updatedFood.setProtein(foodDto.getProtein());
                updatedFood.setCarbohydrate(foodDto.getCarbohydrate());
                updatedFood.setFat(foodDto.getFat());
                updatedFood.setAmount(foodDto.getAmount());

                foodMapper.updateFood(updatedFood);
            } else {
                // 🛠 새로운 음식이면 INSERT
                FoodList newFood = new FoodList();
                newFood.setMenu(foodDto.getMenu());
                newFood.setKcal(foodDto.getKcal());
                newFood.setProtein(foodDto.getProtein());
                newFood.setCarbohydrate(foodDto.getCarbohydrate());
                newFood.setFat(foodDto.getFat());
                newFood.setAmount(foodDto.getAmount());
                newFood.setFoodPk(foodPk); // 기존 식사에 연결된 foodPk 사용

                foodMapper.insertFoodList(newFood);
            }
        }
    }

    public List<FoodListDto> getFoodList(int userPk) {
        return foodMapper.getFoodList(userPk);
    }

    // 그래프 통계
    public List<WeeklyFoodStatsDto> getWeeklyFoodStats(int userPk) {
        return foodMapper.getWeeklyFoodStats(userPk);
    }




}
