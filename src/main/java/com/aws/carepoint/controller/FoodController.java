package com.aws.carepoint.controller;

import com.aws.carepoint.dto.FoodDTO;
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
    public List<FoodDTO> searchFood(@RequestParam(name = "query") String query) {
        return foodService.searchFood(query);
    }

    }



