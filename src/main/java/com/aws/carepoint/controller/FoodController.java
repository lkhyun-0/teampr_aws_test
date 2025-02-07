package com.aws.carepoint.controller;

import com.aws.carepoint.domain.Food;
import com.aws.carepoint.domain.FoodList;
import com.aws.carepoint.dto.FoodDto;
import com.aws.carepoint.dto.FoodListDto;
import com.aws.carepoint.dto.FoodRecordRequest;
import com.aws.carepoint.dto.UpdateMealRequest;
import com.aws.carepoint.service.FoodService;
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

    // JavaScript에서 AJAX로 데이터를 가져올 때 호출할 JSON API
    @GetMapping("/foodList/data")
    @ResponseBody
    public List<FoodListDto> getFoodList(@RequestParam(value = "userPk", required = false, defaultValue = "1") int userPk) {
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
    public String recordFood(@RequestBody FoodRecordRequest request) {
        try {
            foodService.recordFood(request);
            return "success";
        } catch (Exception e) {
            return "error";
        }
    }


    //특정 날짜의 식단 가져오기
    @GetMapping("/detail/data")
    @ResponseBody
    public List<FoodList> getFoodByDate(@RequestParam("userPk") int userPk, @RequestParam("selectDate") String selectDate) {
        return foodService.getFoodByDate(userPk, selectDate);
    }

    // 음식 삭제
    @DeleteMapping("/delete")
    @ResponseBody
    public String deleteFood(@RequestBody Map<String, Object> request) {
        int foodListPk = (int) request.get("foodListPk");
        String selectDate = (String) request.get("selectDate");
        String foodType = (String) request.get("foodType");
        int userPk = (int) request.get("userPk");

        foodService.deleteFood(foodListPk, selectDate, foodType, userPk);
        return "success";
    }

    @PostMapping("/updateMeal")
    @ResponseBody
    public String updateMeal(@RequestBody UpdateMealRequest request) {
        foodService.updateMeal(request);
        return "success";
    }





}



