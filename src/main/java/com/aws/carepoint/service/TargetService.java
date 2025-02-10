package com.aws.carepoint.service;

import com.aws.carepoint.dto.TargetDto;
import com.aws.carepoint.mapper.ExerciseMapper;
import com.aws.carepoint.mapper.TargetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

@Service
public class TargetService {

    private final TargetMapper targetMapper;

    @Autowired
    public TargetService(TargetMapper targetMapper) {
        this.targetMapper = targetMapper;
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
        targetMapper.insertTarget(targetDto);
    }

    // ✅ 이번 주 목표 데이터 가져오기
    public TargetDto getCurrentWeekTarget(int userPk) {
        TargetDto targetDto = targetMapper.findCurrentWeekTarget(userPk);
        return targetDto;
    }
}
