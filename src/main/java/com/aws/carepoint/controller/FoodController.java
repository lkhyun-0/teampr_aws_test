package com.aws.carepoint.controller;

//import com.aws.carepoint.entity.FoodInfo;
//import com.aws.carepoint.service.FoodInfoService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/food")
public class FoodController {

//    private final FoodInfoService foodInfoService;
//
//    public FoodController(FoodInfoService foodInfoService) {
//        this.foodInfoService = foodInfoService;
//    }

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


//    // 특정 음식명을 기반으로 API 데이터를 가져와 DB에 저장
//    @PostMapping("/fetch")
//    public String fetchFoodData(@RequestParam String menu) {
//        foodInfoService.fetchAndSaveFoodData(menu);
//        return "Food data fetched and saved!";
//    }
//
//    // 음식명 검색 API (AJAX 요청)
//    @GetMapping("/search")
//    public List<FoodInfo> searchFood(@RequestParam String menu) {
//        return foodInfoService.searchFoodByName(menu);
//    }






    }



