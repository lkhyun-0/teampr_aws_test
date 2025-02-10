package com.aws.carepoint.controller;

import com.aws.carepoint.dto.TargetDto;
import com.aws.carepoint.service.ExerciseService;
import com.aws.carepoint.service.TargetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.lang.annotation.Target;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/target")
public class TargetController {

    private final TargetService targetService;

    @Autowired
    public TargetController(TargetService targetService) { // ✅ 생성자로 주입
        this.targetService = targetService;
    }

    @ResponseBody
    @PostMapping("/saveTarget")
    public ResponseEntity<Map<String, String>> saveTarget(@RequestBody TargetDto targetDto) {
        targetService.saveTarget(targetDto);

        // 응답을 JSON 형태로 반환
        Map<String, String> response = new HashMap<>();
        response.put("message", "이번 주 목표가 저장되었습니다.");

        return ResponseEntity.ok(response);
    }

    // 이번주 목표 데이터 가져오기
    @GetMapping("/current-week")
    public ResponseEntity<TargetDto> getCurrentWeekTarget(@RequestParam("userPk") int userPk) {
        TargetDto targetDto = targetService.getCurrentWeekTarget(userPk);
        if (targetDto == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(targetDto);
    }

    // ✅ target_count 증가 API
    @ResponseBody
    @PostMapping("/update-target-count")
    public ResponseEntity<?> updateTargetCount(@RequestBody Map<String, Integer> request) {
        Integer userPk = request.get("userPk");
        if (userPk == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid userPk"));
        }

        boolean isUpdated = targetService.incrementTargetCount(userPk);
        if (isUpdated) {
            return ResponseEntity.ok(Map.of("success", true));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update target count"));
        }
    }

    // 이번 주 target_count 업데이트 여부 체크
    @GetMapping("/has-updated-this-week")
    public ResponseEntity<Boolean> hasUpdatedThisWeek(@RequestParam int userPk) {
        boolean hasUpdated = targetService.hasUpdatedThisWeek(userPk);
        return ResponseEntity.ok(hasUpdated);
    }

    // 해당 회원이 운동 기록한 횟수 가져오기
    @ResponseBody
    @GetMapping("/count")
    public int getTargetCount(@RequestParam("userPk") int userPk) {
        return targetService.getTargetCount(userPk);
    }

}
