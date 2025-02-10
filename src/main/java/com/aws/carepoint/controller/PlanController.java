package com.aws.carepoint.controller;

import com.aws.carepoint.dto.HospitalDto;
import com.aws.carepoint.service.CommentService;
import com.aws.carepoint.service.PlanService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/plan")
public class PlanController {

    @Autowired
    private PlanService planService;

    @GetMapping("/plan")
    public String plan(Model model, HttpSession session) {

        Integer userPk = (Integer) session.getAttribute("userPk");

        List<HospitalDto> hospitalList = planService.getAllHospital(userPk);
        model.addAttribute("hospitalList", hospitalList);
        return "plan/plan";
    }

    // AJAX 요청으로 일정 목록 가져오기
    @GetMapping("/getAllPlansAjax")
    public ResponseEntity<List<Map<String, Object>>> getAllPlansAjax(HttpSession session) {
        Integer userPk = (Integer) session.getAttribute("userPk");

        List<HospitalDto> hospitalList = planService.getAllHospital(userPk);

        List<Map<String, Object>> events = hospitalList.stream().map(hospitalDto -> {
            Map<String, Object> event = new HashMap<>();
            event.put("title", hospitalDto.getHospitalName());
            event.put("start", hospitalDto.getSelectDate());
            return event;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(events);
    }

    @GetMapping("/hospital")
    public String hospital() {
        return "plan/hospital";
    }

    @PostMapping("/saveHospital")
    @ResponseBody
    public ResponseEntity<?> saveHospital(
            @RequestBody HospitalDto hospitalDto,
            HttpSession session
    ) {
        try {
            Integer userPk = (Integer) session.getAttribute("userPk");

            if (userPk == null || userPk < 1) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("로그인이 필요합니다.");
            }

            // 🔹 필수 값 확인
            if (hospitalDto.getHospitalName() == null || hospitalDto.getSelectDate() == null || hospitalDto.getSelectTime() == null) {
                return ResponseEntity.badRequest().body("필수 입력값이 누락되었습니다.");
            }

            hospitalDto.setUserPk(userPk);

            // 🔹 병원 저장 로직 실행
            planService.saveHospital(hospitalDto);
            return ResponseEntity.ok("일정 등록 완료");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("서버 오류: " + e.getMessage());
        }
    }
}
