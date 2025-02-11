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

    @GetMapping("/hospital")
    public String hospital() {
        return "plan/hospital";
    }

    @GetMapping("/plan")
    public String plan(
            Model model,
            HttpSession session
    ) {

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
            event.put("id", hospitalDto.getHospitalPk());
            event.put("title", hospitalDto.getHospitalName());
            event.put("start", hospitalDto.getSelectDate());
            return event;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(events);
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

            // 필수 값 확인
            if (hospitalDto.getHospitalName() == null || hospitalDto.getSelectDate() == null || hospitalDto.getSelectTime() == null) {
                return ResponseEntity.badRequest().body("필수 입력값이 누락되었습니다.");
            }

            hospitalDto.setUserPk(userPk);

            HospitalDto savedHospital = planService.saveHospital(hospitalDto);
            Map<String, Object> response = new HashMap<>();
            response.put("id", savedHospital.getHospitalPk()); // 저장된 일정 ID
            response.put("title", savedHospital.getHospitalName()); // 일정 제목
            response.put("start", savedHospital.getSelectDate()); // 일정 날짜
            response.put("allDay", true);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("서버 오류: " + e.getMessage());
        }
    }

    @GetMapping("/getHospitalRecent")
    public ResponseEntity<HospitalDto> getHospitalRecent(@RequestParam("hospitalName") String hospitalName) {
        HospitalDto hospitalDto = planService.getHospitalRecent(hospitalName);

        if (hospitalDto != null) {
            return ResponseEntity.ok(hospitalDto);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/getHospitalDetail/{hospitalPk}")
    @ResponseBody
    public HospitalDto getHospitalDetail(
            @PathVariable("hospitalPk") int hospitalPk,
            @RequestParam("selectDate") String selectDate
    ) {
        return planService.getHospitalDetail(hospitalPk, selectDate); // 일정 상세 조회
    }
}
