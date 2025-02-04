package com.aws.carepoint.service;

import com.aws.carepoint.domain.ExerciseApiEntity;
import com.aws.carepoint.dto.ExerciseApiResponse;
import com.aws.carepoint.dto.ExerciseDto;
import com.aws.carepoint.mapper.ExerciseMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class ExerciseService {

    private final ExerciseMapper exerciseMapper;

    @Autowired
    public ExerciseService(ExerciseMapper exerciseMapper) {
        this.exerciseMapper = exerciseMapper;
    }

    public void save(ExerciseDto exerciseDto) {

        System.out.println("컨트롤러에서 DTO 찍어보기~" + exerciseDto);
        System.out.println("운동명 : " + exerciseDto.getExerciseName());
        System.out.println("MET 계수 : " + exerciseDto.getMetValue());

        // DTO -> Entity 변환
        ExerciseApiEntity exerciseEntity = new ExerciseApiEntity();
        exerciseEntity.setExerciseName(exerciseDto.getExerciseName());
        exerciseEntity.setMetValue(Double.parseDouble(exerciseDto.getMetValue())); // String -> Double 변환

        // DB 저장
        exerciseMapper.insertExercise(exerciseEntity);
    }
}