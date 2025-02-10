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



    @GetMapping("/foodRecord")
    public String showRecordPage() {

        return "food/foodRecord"; // templates/food/foodRecord.html
    }

    // 📌 특정 사용자의 식단 목록 조회
//    @GetMapping("/foodList")
//    @ResponseBody
//    public List<Food> getFoodList(@RequestParam("userPk") int userPk) {
//        return foodService.getFoodList(userPk);
//    }

    // 사용자가 브라우저에서 /food/foodList 방문 시, HTML 반환
    @GetMapping("/foodList")
    public String showListPage() {
        return "food/foodList";  // templates/food/foodList.html
    }

    @GetMapping("/foodList/data")
    @ResponseBody
    public List<FoodListDto> getFoodList(@SessionAttribute("userPk") int userPk) {
        return foodService.getFoodList(userPk);
    }

    @GetMapping("/analysis")
    public String showAnalysisPage() {

        return "food/analysis"; // templates/food/analysis.html
    }

    @GetMapping("/detail")
    public String showDetailPage() {

        return "food/detail"; // templates/food/analysis.html
    }

    @GetMapping("/recom")
    public String showRecomPage() {

        return "food/recom"; // templates/food/analysis.html
    }

    @GetMapping("/recomResult")
    public String showRecomResultPage() {

        return "food/recomResult"; // templates/food/analysis.html
    }


    // 2. 검색 API - JSON 데이터 반환
    @GetMapping("/search")
    @ResponseBody
    public List<FoodDto> searchFood(@RequestParam(name = "query") String query) {
        return foodService.searchFood(query);
    }

    @PostMapping("/record")
    @ResponseBody
    public String recordFood(@RequestBody FoodRecordRequest request,
                             @SessionAttribute("userPk") int userPk) {
        try {
            request.setUserPk(userPk);
            foodService.recordFood(request);
            return "success";
        } catch (Exception e) {
            return "error";
        }
    }


    //특정 날짜의 식단 가져오기
    @GetMapping("/detail/data")
    @ResponseBody
    public List<FoodList> getFoodByDate(@SessionAttribute("userPk") int userPk,
                                        @RequestParam("selectDate") String selectDate) {
        return foodService.getFoodByDate(userPk, selectDate);
    }

    // 음식 삭제
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

    @PostMapping("/updateMeal")
    @ResponseBody
    public String updateMeal(@RequestBody UpdateMealRequest request,
                             @SessionAttribute("userPk") int userPk) {
        request.setUserPk(userPk);
        foodService.updateMeal(request);
        return "success";
    }


    // ai api
    @GetMapping("/recommend")
    @ResponseBody
    public ResponseEntity<String> recommendMeal(@RequestParam(name = "goal") String goal) {
        String recommendation = mealService.getMealRecommendation(goal).block(); // Mono → String 변환
        return recommendation != null ? ResponseEntity.ok(recommendation) : ResponseEntity.badRequest().build();
    }

    @GetMapping("/foodResult")
    public String showResultPage() {
        return "food/foodResult"; // templates/food/foodresult.html
    }


    @GetMapping("/weeklyStats")
    @ResponseBody
    public List<WeeklyFoodStatsDto> getWeeklyFoodStats(@SessionAttribute("userPk") int userPk) {
        return foodService.getWeeklyFoodStats(userPk);
    }

    // 사용자 나이와 성별 가져오기
    @GetMapping("/getUserDetail")
    @ResponseBody
    public DetailDto getUserDetail(@SessionAttribute("userPk") int userPk) {
        return detailMapper.getUserDetail(userPk);
    }




}



