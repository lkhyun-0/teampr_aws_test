package com.aws.carepoint.controller;

import com.aws.carepoint.domain.FoodList;
import com.aws.carepoint.dto.FoodDto;
import com.aws.carepoint.dto.FoodRecordRequest;
import com.aws.carepoint.service.FoodService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/food")
@RequiredArgsConstructor
public class FoodController {

    private final FoodService foodService;

    @GetMapping("/foodRecord")
    public String showRecordPage() {

        return "food/foodRecord"; // templates/food/foodRecord.html
    }

    @GetMapping("/foodList")
    public String showListPage() {

        return "food/foodList"; // templates/food/foodList.html
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

    //음식 삭제
    @DeleteMapping("/delete")
    @ResponseBody
    public String deleteFood(@RequestParam("foodListPk") int foodListPk) {
        foodService.deleteFood(foodListPk);
        return "success";
    }

    //음식 수정
    @PostMapping("/update")
    @ResponseBody
    public String updateFood(@RequestBody FoodList foodList) {
        foodService.updateFood(foodList);
        return "success";
    }

}



