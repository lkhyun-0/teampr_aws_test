package com.aws.carepoint.controller;

import com.aws.carepoint.domain.Food;
import com.aws.carepoint.domain.FoodList;
import com.aws.carepoint.dto.*;
import com.aws.carepoint.mapper.DetailMapper;
import com.aws.carepoint.service.FoodService;
import com.aws.carepoint.service.MealService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/food")
@RequiredArgsConstructor
public class FoodController {

    private final FoodService foodService;
    private final MealService mealService;
    private final DetailMapper detailMapper;


    // 식단 기록 화면
    @GetMapping("/foodRecord")
    public String showRecordPage() {

        return "food/foodRecord";
    }

    // 식단 목록 화면
    @GetMapping("/foodList")
    public String showListPage() {
        return "food/foodList";
    }

    // 회원 식단 목록 가져오기
    @GetMapping("/foodList/data")
    @ResponseBody
    public List<FoodListDto> getFoodList(@SessionAttribute("userPk") int userPk) {
        return foodService.getFoodList(userPk);
    }

    // 식단 분석 화면
    @GetMapping("/analysis")
    public String showAnalysisPage() {

        return "food/analysis";
    }

    // 식단 기록 상세내용
    @GetMapping("/detail")
    public String showDetailPage() {

        return "food/detail";
    }

    // 식단 추천 화면
    @GetMapping("/recom")
    public String showRecomPage() {

        return "food/recom";
    }

    // 식단 추천 결과 화면
    @GetMapping("/recomResult")
    public String showRecomResultPage() {

        return "food/recomResult";
    }

    // 영양정보 api 검색
    @GetMapping("/search")
    @ResponseBody
    public List<FoodDto> searchFood(@RequestParam(name = "query") String query) {
        return foodService.searchFood(query);
    }

    // 식단 기록하기 
    @PostMapping("/record")
    @ResponseBody
    public String recordFood(@RequestBody FoodRecordRequest request,
                             @SessionAttribute(name = "userPk", required = false) Integer userPk) {
        if (userPk == null) {
            return "Session userPk is null";
        }

        try {
            request.setUserPk(userPk);
            foodService.recordFood(request);
            return "success";
        } catch (Exception e) {
            return "error";
        }
    }
    
    // 회원 식단 데이터 가져오기
    @GetMapping("/detail/data")
    @ResponseBody
    public List<FoodList> getFoodByDate(@SessionAttribute("userPk") int userPk,
                                        @RequestParam("selectDate") String selectDate) {
        return foodService.getFoodByDate(userPk, selectDate);
    }

    // 식단 삭제
    @DeleteMapping("/delete")
    @ResponseBody
    public String deleteFood(@RequestBody Map<String, Object> request,
                             @SessionAttribute("userPk") int userPk) {
        int foodListPk = (int) request.get("foodListPk");
        String selectDate = (String) request.get("selectDate");
        String foodType = (String) request.get("foodType");

        foodService.deleteFood(foodListPk, selectDate, foodType, userPk);
        return "success";
    }

    // 식단 수정
    @PostMapping("/updateMeal")
    @ResponseBody
    public String updateMeal(@RequestBody UpdateMealRequest request,
                             @SessionAttribute("userPk") int userPk) {
        request.setUserPk(userPk);
        foodService.updateMeal(request);
        return "success";
    }
    
    // open ai api 호출
    @GetMapping("/recommend")
    @ResponseBody
    public ResponseEntity<String> recommendMeal(@RequestParam(name = "goal") String goal) {
        String recommendation = mealService.getMealRecommendation(goal).block(); // Mono → String 변환
        return recommendation != null ? ResponseEntity.ok(recommendation) : ResponseEntity.badRequest().build();
    }

    // 식단 추천 결과 화면
    @GetMapping("/foodResult")
    public String showResultPage() {
        return "food/foodResult"; 
    }

    // 회원 식단 분석 
    @GetMapping("/weeklyStats")
    @ResponseBody
    public List<WeeklyFoodStatsDto> getWeeklyFoodStats(@SessionAttribute("userPk") int userPk) {
        return foodService.getWeeklyFoodStats(userPk);
    }

    // 회원 나이와 성별 가져오기
    @GetMapping("/getUserDetail")
    @ResponseBody
    public DetailDto getUserDetail(@SessionAttribute("userPk") int userPk) {
        return detailMapper.getUserDetail(userPk);
    }
}