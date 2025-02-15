package com.aws.carepoint.service;

import com.aws.carepoint.domain.ExerciseApiEntity;
import com.aws.carepoint.dto.*;
import com.aws.carepoint.mapper.ExerciseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
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

    // 운동 종목 선택 팝업에 띄울 리스트 가져오기
    public List<ExerciseApiDto> getExerciseApiList() {
        return exerciseMapper.getExerciseApiList();
    }

    // 운동 정보 DB Insert
    @Transactional
    public void saveExercise(ExerciseDto exerciseDto) {
        // 해당 유저의 detail 테이블에서 몸무게 가져오기
        String weight = exerciseMapper.getDetailWeightValue(exerciseDto.getUserPk());

        // 몸무게 int 변환
        int int_weight = Integer.parseInt(weight);

        // 시간(hour)과 분(minute)을 합쳐서 총 운동 시간 계산
        double totalTime = exerciseDto.getHour() + (exerciseDto.getMinute() / 60.0);

        // MET 지수를 활용하여 kcal 계산
        double kcal = exerciseDto.getMetValue() * int_weight * totalTime;

        // double에서 int로 변환하여 exerciseDto에 저장
        int int_kcal = (int) kcal;
        exerciseDto.setKcal(int_kcal);

        // 운동 DB 저장
        exerciseMapper.insertExercise(exerciseDto);
        // 목표 운동 횟수 업데이트
        exerciseMapper.updateExerciseTarget(exerciseDto.getUserPk(), exerciseDto.getRegDate(), int_kcal);
    }

    // 운동 종목 선택 팝업에 띄울 리스트 가져오기
    public List<ExerciseDto> getAllExercises(int userPk) {
        List<ExerciseDto> exercises = exerciseMapper.getAllExercises(userPk);
        return exercises;
    }

    // 해당 회원이 운동 기록한 횟수 가져오기
    public int getExerciseCount(int userPk) {
        return exerciseMapper.getExerciseCount(userPk);
    }

    // 오늘 날짜에 운동을 기록했는지 확인
    public boolean hasTodayExerciseData(int userPk) {
        return exerciseMapper.hasTodayExerciseData(userPk) > 0;
    }

    public void deleteExercise(int exercisePk) {
        exerciseMapper.deleteExercise(exercisePk);
    }
}