package com.aws.carepoint.mapper;

import com.aws.carepoint.dto.GraphDto;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface GraphMapper {

    @Select("SELECT graph_pk, weight, blood_press, blood_sugar, reg_date, user_pk FROM graph WHERE user_pk = #{userPk} ORDER BY reg_date")
    @Results(id = "graphResultMap", value = {
            @Result(property = "bloodSugar", column = "blood_sugar"),
            @Result(property = "bloodPress", column = "blood_press"),
            @Result(property = "userPk", column = "user_pk")
    })
    List<GraphDto> getGraphData(int userPk);

    // 그래프 데이터 삽입 (target_pk 자동 할당)
    @Insert("INSERT INTO graph (weight, blood_press, blood_sugar, reg_date, user_pk, target_pk) " +
            "VALUES (#{weight}, #{bloodPress}, #{bloodSugar}, NOW(), #{userPk}, " +
            "(SELECT target_pk FROM target WHERE user_pk = #{userPk} " +
            "AND NOW() BETWEEN start_date AND end_date LIMIT 1))")
    @Results(id = "graphResultMap", value = {
            @Result(property = "bloodSugar", column = "blood_sugar"),
            @Result(property = "bloodPress", column = "blood_press"),
            @Result(property = "userPk", column = "user_pk")
    })
    @Options(useGeneratedKeys = true, keyProperty = "graphPk") // 자동 증가된 PK 가져오기
    void insertGraph(GraphDto graphDto);

    // 그래프 데이터 수정
    @Update("UPDATE graph SET blood_sugar = #{bloodSugar}, blood_press = #{bloodPress}, " +
            "weight = #{weight} WHERE user_pk = #{userPk}")
    @Results(id = "graphResultMap", value = {
            @Result(property = "bloodSugar", column = "blood_sugar"),
            @Result(property = "bloodPress", column = "blood_press"),
            @Result(property = "userPk", column = "user_pk")
    })
    void updateGraph(GraphDto graphDto);

    // 목표 테이블의 value_count 증가
    @Update("UPDATE target SET value_count = value_count + 1 " +
            "WHERE target_pk = (SELECT target_pk FROM target WHERE user_pk = #{userPk} " +
            "AND NOW() BETWEEN start_date AND end_date LIMIT 1)")
    void updateValueCount(@Param("userPk") int userPk, @Param("regDate") String regDate);

    // user_detail의 weight도 최신으로 업데이트
    @Update("UPDATE user_detail SET weight = ${weight} " +
            "WHERE user_pk = #{userPk}")
    void updateWeightValue(@Param("weight") int weight, @Param("userPk") int userPk);

    @Select("""
        SELECT COUNT(*) FROM graph 
        WHERE CURDATE() = reg_date -- 'date_column'은 저장된 날짜 필드
        AND user_pk = #{userPk}
    """)
    int hasTodayGraphData(@Param("userPk") int userPk);

}

