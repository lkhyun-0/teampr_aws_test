package com.aws.carepoint.service;

import com.aws.carepoint.domain.ExerciseApiEntity;
import com.aws.carepoint.mapper.ExerciseApiMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class ExerciseService {

    private final RestTemplate restTemplate;
    private final ExerciseApiMapper exerciseApiMapper;

    public ExerciseService(RestTemplate restTemplate, ExerciseApiMapper exerciseApiMapper) {
        this.restTemplate = restTemplate;
        this.exerciseApiMapper = exerciseApiMapper;
    }

    @Transactional
    public void fetchAndSaveExercise() {
        // 1) URL에 보안키를 직접 추가 (API 문서에 맞게 작성)


        String originalServiceKey = "5I80mz4CdqCYGI%2FVEWpNMZIFripbTsu6m6cSduhb6Hd1waDVCXYm820lfpRBuaWxYRzN%2BbaFVnqs%2BjwzQOBmGQ%3D%3D";

        String url = "https://api.odcloud.kr/api/15068730/v1/uddi:12fe14fb-c8ca-47b1-9e53-97a93cb214ed"
                + "?page=1&perPage=10&serviceKey=" + originalServiceKey;

        String response = restTemplate.getForObject(url, String.class);
        System.out.println(response);

    }
}
