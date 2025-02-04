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
        exerciseService.fetchAndSaveExercise();
        return "데이터를 성공적으로 가져와서 DB에 저장했습니다.";
    }
}
