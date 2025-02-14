package com.aws.carepoint.controller;

import com.aws.carepoint.dto.ExerciseApiDto;
import com.aws.carepoint.dto.ExerciseDto;
import com.aws.carepoint.dto.GraphDto;
import com.aws.carepoint.dto.TargetDto;
import com.aws.carepoint.service.ExerciseService;
import jakarta.servlet.http.HttpSession;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/exercise")
public class ExerciseController {

    private final ExerciseService exerciseService;

    @Autowired
    public ExerciseController(ExerciseService exerciseService) { // ✅ 생성자로 주입
        this.exerciseService = exerciseService;
    }

    // 운동 메인페이지
    @GetMapping("/exerciseMain")
    public String exerciseMain(Model model, HttpSession session) {

        int userPk = (Integer) session.getAttribute("userPk");

        model.addAttribute("userPk", userPk);
        return "exercise/exerciseMain";
    }

    // 운동 목록 불러오기 API
    @ResponseBody
    @GetMapping("/apiList")
    public List<ExerciseApiDto> apiList() {
        return exerciseService.getExerciseApiList();
    }

    // 운동 정보 저장하기
    @ResponseBody
    @PostMapping("/saveExercise")
    public ResponseEntity<Map<String, String>> saveExercise(@RequestBody ExerciseDto exerciseDto, HttpSession session) {

        int userPk = (Integer) session.getAttribute("userPk");
        exerciseDto.setUserPk(userPk);
        exerciseService.saveExercise(exerciseDto);

        // JSON 응답으로 변경
        Map<String, String> response = new HashMap<>();
        response.put("message", "운동 기록이 저장되었습니다!");

        return ResponseEntity.ok(response);
    }

    // 📌 저장된 운동 기록 가져오기 (캘린더에 반영)
    @ResponseBody
    @GetMapping("/getExerciseEvents")
    public List<ExerciseDto> getExerciseEvents(HttpSession session) {
        int userPk = (int) session.getAttribute("userPk");
        return exerciseService.getAllExercises(userPk);
    }

    // 해당 회원이 운동 기록한 횟수 가져오기
    @ResponseBody
    @GetMapping("/count")
    public int getExerciseCount(@RequestParam("userPk") int userPk) {
        return exerciseService.getExerciseCount(userPk);
    }

    // ✅ 오늘 운동 데이터가 있는지 확인
    @ResponseBody
    @GetMapping("/has-today-exercise")
    public ResponseEntity<Boolean> hasTodayExerciseData(@RequestParam("userPk") int userPk) {
        boolean hasData = exerciseService.hasTodayExerciseData(userPk);
        return ResponseEntity.ok(hasData);
    }

















    @ResponseBody
    @GetMapping("/apiSave")
    public String callapihttp() {
        StringBuilder result = new StringBuilder();
        try {
            String urlstr = "https://api.odcloud.kr/api/15068730/v1/uddi:12fe14fb-c8ca-47b1-9e53-97a93cb214ed?" +
                    "page=1" +
                    "&perPage=259" +
                    "&serviceKey=5I80mz4CdqCYGI%2FVEWpNMZIFripbTsu6m6cSduhb6Hd1waDVCXYm820lfpRBuaWxYRzN%2BbaFVnqs%2BjwzQOBmGQ%3D%3D";
            URL url = new URL(urlstr);
            HttpURLConnection urlconnection = (HttpURLConnection) url.openConnection();
            urlconnection.setRequestMethod("GET");

            BufferedReader br = new BufferedReader(new InputStreamReader(urlconnection.getInputStream(), "UTF-8"));
            String line;
            while ((line = br.readLine()) != null) {
                result.append(line);
            }
            br.close();

            // JSON Parsing
            JSONObject jsonObject = new JSONObject(result.toString());
            JSONArray jsonArray = jsonObject.getJSONArray("data");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                String exerciseName = object.optString("운동명", "Unknown");
                String metValue = object.optString("MET 계수", "0.0");

                ExerciseApiDto exerciseApiDto = new ExerciseApiDto();
                exerciseApiDto.setExerciseName(exerciseName);
                exerciseApiDto.setMetValue(metValue);

                exerciseService.save(exerciseApiDto);
            }
            return "✅ 데이터가 저장되었습니다.";
        } catch (Exception e) {
            e.printStackTrace();
            return "에러: " + e.getMessage();
        }
    }
}
