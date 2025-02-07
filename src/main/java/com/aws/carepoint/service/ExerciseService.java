package com.aws.carepoint.service;

import com.aws.carepoint.domain.ExerciseEntity;
import com.aws.carepoint.dto.ExerciseDto;
import com.aws.carepoint.dto.GraphDto;
import com.aws.carepoint.mapper.ExerciseMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        ExerciseEntity exerciseEntity = new ExerciseEntity();
        exerciseEntity.setExerciseName(exerciseDto.getExerciseName());
        exerciseEntity.setMetValue(Double.parseDouble(exerciseDto.getMetValue())); // String -> Double 변환

        // DB 저장
        exerciseMapper.saveExerciseApi(exerciseEntity);
    }

    public void saveGraph(GraphDto graphDto) {
        exerciseMapper.insertGraph(graphDto);
    }
}