package com.aws.carepoint.controller;

import com.aws.carepoint.dto.ExerciseDto;
import com.aws.carepoint.service.ExerciseService;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.json.JSONParser;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@RestController
public class RestTestController {

    private final ExerciseService exerciseService;

    @Autowired
    public RestTestController(ExerciseService exerciseService) { // ✅ 생성자로 주입
        this.exerciseService = exerciseService;
    }

    @GetMapping("/apitest")
    public String callapihttp() {
        StringBuilder result = new StringBuilder();
        try {
            String urlstr = "https://api.odcloud.kr/api/15068730/v1/uddi:12fe14fb-c8ca-47b1-9e53-97a93cb214ed?" +
                    "page=1" +
                    "&perPage=10" +
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

                ExerciseDto exerciseDto = new ExerciseDto();
                exerciseDto.setExerciseName(exerciseName);
                exerciseDto.setMetValue(metValue);

                exerciseService.save(exerciseDto);
            }
            return "성공";
        } catch (Exception e) {
            e.printStackTrace();
            return "에러: " + e.getMessage();
        }
    }
}
