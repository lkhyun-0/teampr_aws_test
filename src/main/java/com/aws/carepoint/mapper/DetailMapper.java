package com.aws.carepoint.mapper;

import com.aws.carepoint.dto.DetailDto;
import com.aws.carepoint.dto.UsersDto;
import org.apache.ibatis.annotations.*;

@Mapper
public interface DetailMapper {
    // 회원 상세정보 입력 !
    @Insert("INSERT INTO user_detail (age, weight, height, gender, sick_type, sick_detail, smoke, exercise_cnt, drink, target_count, reg_date, update_date, user_pk) " +
            "VALUES (#{age}, #{weight}, #{height}, #{gender}, #{sickType}, #{sickDetail}, #{smoke}, #{exerciseCnt}, #{drink}, #{targetCount}, NOW(), NOW(), #{userPk})")
    @Options(useGeneratedKeys = true, keyProperty = "detailPk", keyColumn = "detail_pk")
    void insertDetail(DetailDto detailDto);

    @Select("SELECT * FROM user_detail WHERE user_pk = #{userPk} LIMIT 1")
    DetailDto getUserDetail(int userPk);

    @Select("SELECT * FROM user_detail WHERE user_pk = #{userPk} ORDER BY user_pk DESC LIMIT 1")
    @Results(id = "detailResultMap", value = {
            @Result(property = "detailPk", column = "detail_pk"),
            @Result(property = "age", column = "age"),
            @Result(property = "weight", column = "weight"),
            @Result(property = "gender", column = "gender"),
            @Result(property = "height", column = "height"),
            @Result(property = "sickType", column = "sick_type"),
            @Result(property = "sickDetail", column = "sick_detail"),
            @Result(property = "smoke", column = "smoke"),
            @Result(property = "exerciseCnt", column = "exercise_cnt"),
            @Result(property = "drink", column = "drink"),
            @Result(property = "photo", column = "photo"),
            @Result(property = "targetCount", column = "targetCount"),
            @Result(property = "regDate", column = "reg_date"),
            @Result(property = "updateDate", column = "update_date"),
            @Result(property = "userPk", column = "user_pk")
    })
    DetailDto getUserDetailById(int userPk);        // 마이페이지에서 보여줘야할 데이터들

    @Update("UPDATE user_detail " +
            "SET height = IFNULL(#{height}, height), " +
            "weight = IFNULL(#{weight}, weight), " +
            "smoke = IFNULL(#{smoke}, smoke), " +
            "drink = IFNULL(#{drink}, drink), " +
            "update_date = NOW() " +
            "WHERE user_pk = #{userPk}")
    int updateDetailInfo(DetailDto detailDto);


    // 프사 변경
    @Update("UPDATE user_detail SET photo = #{imagePath} WHERE user_pk = #{userPk}")
    void updateProfileImage(@Param("userPk") int userPk, @Param("imagePath") String imagePath);

}
