package com.aws.carepoint.mapper;

import com.aws.carepoint.dto.TargetDto;
import org.apache.ibatis.annotations.*;

@Mapper
public interface TargetMapper {

    @Insert("INSERT INTO target (exercise_target, value_target, kcal_target, start_date, end_date, user_pk) " +
            "VALUES (#{exerciseTarget}, #{valueTarget}, #{kcalTarget}, #{startDate}, #{endDate}, #{userPk})")
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
            "WHERE user_pk = #{userPk} " +
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

    // ✅ target_count 증가 쿼리 실행
    @Update("""
        UPDATE user_detail 
        SET target_count = target_count + 1, last_target_update = CURDATE() 
        WHERE user_pk = #{userPk} 
        AND (last_target_update IS NULL OR last_target_update < CURDATE() - INTERVAL WEEKDAY(CURDATE()) DAY)
    """)
    int incrementTargetCount(@Param("userPk") int userPk);

    // 이번 주 target_count 업데이트 여부 체크
    @Select("""
        SELECT COUNT(*) FROM user_detail 
        WHERE user_pk = #{userPk} 
        AND last_target_update >= (CURDATE() - INTERVAL WEEKDAY(CURDATE()) DAY)
    """)
    int hasUpdatedThisWeek(@Param("userPk") int userPk);

    // 해당 회원이 운동 기록한 횟수 가져오기
    @Select("SELECT target_count FROM user_detail WHERE user_pk = #{userPk}")
    int getTargetCount(@Param("userPk") int userPk);
}
