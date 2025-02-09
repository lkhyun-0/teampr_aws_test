package com.aws.carepoint.mapper;

import com.aws.carepoint.domain.ExerciseApiEntity;
import com.aws.carepoint.dto.ExerciseApiDto;
import com.aws.carepoint.dto.ExerciseDto;
import com.aws.carepoint.dto.GraphDto;
import com.aws.carepoint.dto.TargetDto;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ExerciseMapper {

    @Insert("INSERT INTO exercise_Api (exercise_name, MET) VALUES (#{exerciseName}, #{metValue})")
    void saveExerciseApi(ExerciseApiEntity exercise);

    // ✅ 그래프 데이터 삽입 (target_pk 자동 할당)
    @Insert("INSERT INTO graph (weight, blood_press, blood_sugar, reg_date, user_pk, target_pk) " +
            "VALUES (#{weight}, #{bloodPress}, #{bloodSugar}, NOW(), 2, " +
            "(SELECT target_pk FROM target WHERE user_pk = 2 " +
            "AND NOW() BETWEEN start_date AND end_date LIMIT 1))")
    @Results(id = "graphResultMap", value = {
            @Result(property = "bloodSugar", column = "blood_sugar"),
            @Result(property = "bloodPress", column = "blood_press"),
            @Result(property = "userPk", column = "user_pk")
    })
    @Options(useGeneratedKeys = true, keyProperty = "graphPk") // 자동 증가된 PK 가져오기
    void insertGraph(GraphDto graphDto);

    // ✅ 목표 테이블의 value_count 증가
    @Update("UPDATE target SET value_count = value_count + 1 " +
            "WHERE target_pk = (SELECT target_pk FROM target WHERE user_pk = 2 " +
            "AND NOW() BETWEEN start_date AND end_date LIMIT 1)")
    void updateValueCount(@Param("userPk") int userPk, @Param("regDate") String regDate);

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

    @Select("SELECT exercise_name, MET FROM exercise_API")  // exercise_table이 실제 테이블명
    @Results(id = "exerciseApiResultMap", value = {
            @Result(property = "exerciseName", column = "exercise_name"),
            @Result(property = "metValue", column = "MET")
    })
    List<ExerciseApiDto> getExerciseList();

    @Insert("INSERT INTO exercise (exercise_name, MET, reg_date, `hour`, `minute`, user_pk, target_pk) " +
            "VALUES (#{exerciseName}, #{metValue}, #{regDate}, #{hour}, #{minute}, 2, " +
            "(SELECT target_pk FROM target WHERE user_pk = 2 " +
            "AND #{regDate} BETWEEN start_date AND end_date LIMIT 1))")
    @Results(id = "exerciseResultMap", value = {
            @Result(property = "exerciseName", column = "exercise_name"),
            @Result(property = "metValue", column = "MET"),
            @Result(property = "regDate", column = "reg_date"),
            @Result(property = "userPk", column = "user_pk")
    })
    @Options(useGeneratedKeys = true, keyProperty = "exercisePk") // 자동 증가된 PK 가져오기
    void insertExercise(ExerciseDto exerciseDto);

    @Update("UPDATE target SET exercise_count = exercise_count + 1 " +
            "WHERE target_pk = (SELECT target_pk FROM target WHERE user_pk = 2 " +
            "AND #{regDate} BETWEEN start_date AND end_date LIMIT 1)")
    @Results(id = "exerciseResultMap", value = {
            @Result(property = "userPk", column = "user_pk")
    })
    void updateExerciseCount(@Param("userPk") int userPk, @Param("regDate") String regDate);

    @Select("SELECT * FROM exercise")  // exercise_table이 실제 테이블명
    @Results(id = "exerciseResultMap", value = {
            @Result(property = "exerciseName", column = "exercise_name"),
            @Result(property = "metValue", column = "MET"),
            @Result(property = "regDate", column = "reg_date"),
            @Result(property = "userPk", column = "user_pk")
    })
    List<ExerciseDto> getAllExercises();

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
