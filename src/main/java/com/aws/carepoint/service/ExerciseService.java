package com.aws.carepoint.service;

import com.aws.carepoint.domain.ExerciseApiEntity;
import com.aws.carepoint.dto.*;
import com.aws.carepoint.mapper.ExerciseMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.beans.Transient;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Service
public class ExerciseService {

    private final ExerciseMapper exerciseMapper;

    @Autowired
    public ExerciseService(ExerciseMapper exerciseMapper) {
        this.exerciseMapper = exerciseMapper;
    }

    // 운동 api 저장
    public void save(ExerciseApiDto exerciseApiDto) {

        // DTO -> Entity 변환
        ExerciseApiEntity exerciseApiEntity = new ExerciseApiEntity();
        exerciseApiEntity.setExerciseName(exerciseApiDto.getExerciseName());
        exerciseApiEntity.setMetValue(Double.parseDouble(exerciseApiDto.getMetValue())); // String -> Double 변환

        // DB 저장
        exerciseMapper.saveExerciseApi(exerciseApiEntity);
    }

    // 오늘의 수치 저장
    @Transactional
    public void saveGraph(GraphDto graphDto) {
        exerciseMapper.insertGraph(graphDto);
        exerciseMapper.updateValueCount(graphDto.getUserPk(), graphDto.getRegDate()); // target 테이블 업데이트
    }

    // 이번주 목표 저장
    public void saveTarget(TargetDto targetDto) {
        // 오늘 날짜 가져오기
        LocalDate today = LocalDate.now();

        // 이번 주의 시작일 (일요일)
        LocalDate startDate = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));

        // 이번 주의 마지막일 (토요일)
        LocalDate endDate = startDate.plusDays(6);  // 일요일 + 6일 = 토요일

        // TargetDto에 날짜 추가
        targetDto.setStartDate(startDate.toString());
        targetDto.setEndDate(endDate.toString());

        // 목표 저장
        exerciseMapper.insertTarget(targetDto);
    }

    // 운동 종목 선택 팝업에 띄울 리스트 가져오기
    public List<ExerciseApiDto> getExerciseList() {
        return exerciseMapper.getExerciseList();
    }

    // 운동 정보 DB Insert
    @Transactional
    public void saveExercise(ExerciseDto exerciseDto) {
        exerciseMapper.insertExercise(exerciseDto);
        // 목표 운동 횟수 업데이트
        exerciseMapper.updateExerciseCount(exerciseDto.getUserPk(), exerciseDto.getRegDate());
    }

    // 운동 종목 선택 팝업에 띄울 리스트 가져오기
    public List<ExerciseDto> getAllExercises() {
        return exerciseMapper.getAllExercises();
    }

    // ✅ 이번 주 목표 데이터 가져오기
    public TargetDto getCurrentWeekTarget(int userPk) {
        TargetDto targetDto = exerciseMapper.findCurrentWeekTarget(userPk);
        return targetDto;
    }
}