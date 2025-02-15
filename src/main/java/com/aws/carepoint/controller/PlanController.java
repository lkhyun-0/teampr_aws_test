package com.aws.carepoint.controller;

import com.aws.carepoint.dto.HospitalDto;
import com.aws.carepoint.dto.MedicineDto;
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
        List<MedicineDto> medicineList = planService.getAllMedicine(userPk);

        model.addAttribute("hospitalList", hospitalList);
        model.addAttribute("medicineList", medicineList);

        return "plan/plan";
    }

    // 병원
    // AJAX 요청으로 일정 목록 가져오기
    @GetMapping("/getAllHospitalPlansAjax")
    public ResponseEntity<List<Map<String, Object>>> getAllHospitalPlansAjax(HttpSession session) {
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

            boolean exists = planService.checkExistingPlan(hospitalDto.getSelectDate());

            if (exists) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("이미 해당 날짜에 병원 일정이 등록되었습니다.");
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

    @DeleteMapping("/deleteHospital/{hospitalPk}")
    @ResponseBody
    public ResponseEntity<?> deleteHospital(
            @PathVariable("hospitalPk") int hospitalPk,
            HttpSession session
    ) {
        try {
            Integer userPk = (Integer) session.getAttribute("userPk");

            if (userPk == null || userPk < 1) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("로그인이 필요합니다.");
            }

            boolean deleted = planService.deleteHospital(hospitalPk, userPk);

            if (deleted) {
                return ResponseEntity.ok("일정이 삭제되었습니다.");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("일정을 찾을 수 없습니다.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("서버 오류: " + e.getMessage());
        }
    }

    // 약
    // AJAX 요청으로 일정 목록 가져오기
    @GetMapping("/getAllMedicinePlansAjax")
    public ResponseEntity<List<Map<String, Object>>> getAllMedicinePlansAjax(HttpSession session) {
        Integer userPk = (Integer) session.getAttribute("userPk");

        List<MedicineDto> medicineList = planService.getAllMedicine(userPk);

        List<Map<String, Object>> events = medicineList.stream().map(medicineDto -> {
            Map<String, Object> event = new HashMap<>();
            event.put("id", medicineDto.getMedicinePk());
            event.put("title", medicineDto.getMedicineName());
            event.put("start", medicineDto.getStartDate().toString()); // ✅ String (YYYY-MM-DD) 변환
            event.put("end", medicineDto.getEndDate() != null ? medicineDto.getEndDate().toString() : null);
            event.put("color", medicineDto.getColor());

            return event;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(events);
    }

    @PostMapping("/saveMedicine")
    @ResponseBody
    public ResponseEntity<?> saveMedicine(
            @RequestBody MedicineDto medicineDto,
            HttpSession session
    ) {
        try {
            Integer userPk = (Integer) session.getAttribute("userPk");

            if (userPk == null || userPk < 1) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("로그인이 필요합니다.");
            }

            medicineDto.setUserPk(userPk);

            MedicineDto savedMedicine = planService.saveMedicine(medicineDto);
            Map<String, Object> response = new HashMap<>();
            response.put("id", savedMedicine.getMedicinePk()); // 저장된 일정 ID
            response.put("title", savedMedicine.getMedicineName()); // 일정 제목
            response.put("start", savedMedicine.getStartDate()); // 일정 날짜
            response.put("end", savedMedicine.getEndDate());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("서버 오류: " + e.getMessage());
        }
    }

    @GetMapping("/getMedicineRecent")
    public ResponseEntity<MedicineDto> getMedicineRecent(@RequestParam("medicineName") String medicineName) {
        MedicineDto medicineDto = planService.getMedicineRecent(medicineName);

        System.out.println(medicineDto);
        System.out.println(medicineDto.getMedicineType());

        if (medicineDto != null) {
            return ResponseEntity.ok(medicineDto);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/getMedicineDetail")
    @ResponseBody
    public List<MedicineDto> getMedicineDetail(
            @RequestParam("selectDate") String selectDate
    ) {
        return planService.getMedicineDetail(selectDate);
    }

    @DeleteMapping("/deleteMedicine")
    @ResponseBody
    public ResponseEntity<?> deleteMedicine(
            @RequestBody Map<String, List<Integer>> request,
            HttpSession session
    ) {
        try {
            Integer userPk = (Integer) session.getAttribute("userPk");

            if (userPk == null || userPk < 1) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("로그인이 필요합니다.");
            }

            List<Integer> medicinePkList = request.get("medicinePkList");

            if (medicinePkList == null || medicinePkList.isEmpty()) {
                return ResponseEntity.badRequest().body("삭제할 약 일정이 없습니다.");
            }

            System.out.println("medicinePkList: " + medicinePkList);

            boolean deleted = planService.deleteMedicine(medicinePkList, userPk);

            if (deleted) {
                return ResponseEntity.ok("일정이 삭제되었습니다.");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("일정을 찾을 수 없습니다.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("서버 오류: " + e.getMessage());
        }
    }
}
