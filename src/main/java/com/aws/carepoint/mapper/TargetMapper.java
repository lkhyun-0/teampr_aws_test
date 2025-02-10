package com.aws.carepoint.mapper;

import com.aws.carepoint.dto.TargetDto;
import org.apache.ibatis.annotations.*;

@Mapper
public interface TargetMapper {

    @Insert("INSERT INTO target (exercise_target, value_target, kcal_target, start_date, end_date, user_pk) " +
            "VALUES (#{exerciseTarget}, #{valueTarget}, #{kcalTarget}, #{startDate}, #{endDate}, 2)")
    @Results(id = "targetResultMap", value = {
            @Result(property = "exerciseTarget", column = "exercise_target"),
            @Result(property = "valueTarget", column = "value_target"),
            @Result(property = "kcalTarget", column = "kcal_target"),
            @Result(property = "startDate", column = "start_date"),
            @Result(property = "endDate", column = "end_date"),
            @Result(property = "userPk", column = "user_pk")
    })
    void insertTarget(TargetDto targetDto);

    // ✅ 이번 주 목표 데이터 조회
    @Select("SELECT * FROM target " +
            "WHERE user_pk = 2 " +
            "AND CURDATE() BETWEEN start_date AND end_date " +
            "LIMIT 1")
    @Results(id = "targetResultMap", value = {
            @Result(property = "exerciseTarget", column = "exercise_target"),
            @Result(property = "valueTarget", column = "value_target"),
            @Result(property = "kcalTarget", column = "kcal_target"),
            @Result(property = "exerciseCount", column = "exercise_count"),
            @Result(property = "valueCount", column = "value_count"),
            @Result(property = "kcalSum", column = "kcal_sum"),
            @Result(property = "userPk", column = "user_pk")
    })
    TargetDto findCurrentWeekTarget(@Param("userPk") int userPk);
}
