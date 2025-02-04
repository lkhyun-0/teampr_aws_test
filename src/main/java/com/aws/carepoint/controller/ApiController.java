package com.aws.carepoint.controller;

import com.aws.carepoint.service.ExerciseService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final ExerciseService exerciseService;

    public ApiController(ExerciseService exerciseService) {
        this.exerciseService = exerciseService;
    }

    @GetMapping("/fetch")
    public String fetchAndSaveExerciseData() {

        return "✅ 데이터가 성공적으로 저장되었습니다.";
    }
}
