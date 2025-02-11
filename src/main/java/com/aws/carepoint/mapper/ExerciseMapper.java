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

    // DB에 운동 API 저장
    @Insert("INSERT INTO exercise_Api (exercise_name, MET) VALUES (#{exerciseName}, #{metValue})")
    void saveExerciseApi(ExerciseApiEntity exercise);

    // 운동 모달에 띄울 운동명, MET 지수 가져오기
    @Select("SELECT exercise_name, MET FROM exercise_API")  // exercise_table이 실제 테이블명
    @Results(id = "exerciseApiResultMap", value = {
            @Result(property = "exerciseName", column = "exercise_name"),
            @Result(property = "metValue", column = "MET")
    })
    List<ExerciseApiDto> getExerciseApiList();

    // user_detail 에서 해당 회원 weight값 가져오기
    @Select("SELECT weight FROM user_detail WHERE user_pk = #{userPk}")  // exercise_table이 실제 테이블명
    String getDetailWeightValue(@Param("userPk") int userPk);

    // 운동 정보 저장
    @Insert("INSERT INTO exercise (exercise_name, MET, kcal, reg_date, `hour`, `minute`, user_pk, target_pk) " +
            "VALUES (#{exerciseName}, #{metValue}, #{kcal}, #{regDate}, #{hour}, #{minute}, #{userPk}, " +
            "(SELECT target_pk FROM target WHERE user_pk = #{userPk} " +
            "AND #{regDate} BETWEEN start_date AND end_date LIMIT 1))")
    @Results(id = "exerciseResultMap", value = {
            @Result(property = "exerciseName", column = "exercise_name"),
            @Result(property = "metValue", column = "MET"),
            @Result(property = "regDate", column = "reg_date"),
            @Result(property = "userPk", column = "user_pk")
    })
    @Options(useGeneratedKeys = true, keyProperty = "exercisePk") // 자동 증가된 PK 가져오기
    void insertExercise(ExerciseDto exerciseDto);

    // 운동 정보 저장 후 kcal_sum, target 설정
    @Update("UPDATE target SET exercise_count = exercise_count + 1, kcal_sum = kcal_sum + #{kcal} " +
            "WHERE target_pk = (SELECT target_pk FROM target WHERE user_pk = #{userPk} " +
            "AND #{regDate} BETWEEN start_date AND end_date LIMIT 1)")
    @Results(id = "exerciseResultMap", value = {
            @Result(property = "regDate", column = "reg_date"),
            @Result(property = "userPk", column = "user_pk")
    })
    void updateExerciseTarget(@Param("userPk") int userPk, @Param("regDate") String regDate, @Param("kcal") int kcal);

    // 캘린더에 보여줄 운동 데이터 가져오기
    @Select("SELECT * FROM exercise WHERE user_pk = #{userPk}")  // exercise_table이 실제 테이블명
    @Results(id = "exerciseResultMap", value = {
            @Result(property = "exerciseName", column = "exercise_name"),
            @Result(property = "metValue", column = "MET"),
            @Result(property = "regDate", column = "reg_date"),
            @Result(property = "userPk", column = "user_pk")
    })
    List<ExerciseDto> getAllExercises(@Param("userPk") int userPk);

    // 해당 회원이 운동 기록한 횟수 가져오기
    @Select("SELECT COUNT(*) FROM exercise WHERE user_pk = #{userPk}")
    int getExerciseCount(@Param("userPk") int userPk);

    // 오늘 날짜에 운동 데이터 저장했는지 확인
    @Select("""
        SELECT COUNT(*) FROM exercise
        WHERE user_pk = #{userPk} 
        AND reg_date = CURDATE()
    """)
    int hasTodayExerciseData(@Param("userPk") int userPk);

}
