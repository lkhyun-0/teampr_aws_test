package com.aws.carepoint.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/food")
public class FoodController {

    @GetMapping("/record")
    public String showRecordPage() {
        return "food/foodRecord"; // templates/food/foodRecord.html
    }

    @GetMapping("/list")
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


    }



