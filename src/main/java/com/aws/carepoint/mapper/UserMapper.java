package com.aws.carepoint.mapper;

import com.aws.carepoint.dto.UsersDto;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper {
    // 회원 가입
    @Insert("INSERT INTO users (userid, username, userpwd, usernick, phone, email, auth_level, social_login_status, del_status) " +
            "VALUES (#{userId}, #{userName}, #{userPwd}, #{userNick}, #{phone}, #{email}, #{authLevel}, #{socialLoginStatus}, #{delStatus})")
    @Options(useGeneratedKeys = true, keyProperty = "userPk", keyColumn = "user_pk")
    // ✅ 자동 증가된 user_pk 값을 DTO에 반영


    @Results(id = "usersResultsMap", value = {
            @Result(property = "userPk", column = "user_pk"),
            @Result(property = "authLevel", column = "auth_level"),
            @Result(property = "socialLoginStatus", column = "social_login_status"),
            @Result(property = "userId", column = "userid"),
            @Result(property = "userName", column = "username"),
            @Result(property = "userPwd", column = "userpwd"),
            @Result(property = "userNick", column = "usernick"),
            @Result(property = "phone", column = "phone"),
            @Result(property = "email", column = "email"),
            @Result(property = "joinDate", column = "join_date"),
            @Result(property = "updateDate", column = "update_date"),
            @Result(property = "delStatus", column = "del_status"),
            @Result(property = "delDate", column = "delDate"),
    })
    @ResultMap(value = "usersResultMap")

    void insertUser(UsersDto usersDto);

    // 아이디 중복 체크
    @Select("SELECT COUNT(*) FROM users WHERE userid = #{userId}")
    int countByUserId(@Param("userId") String userId);       // 중복체크 오류 해결해야함

    // 닉네임 중복 체크
    @Select("SELECT COUNT(*) FROM users WHERE usernick = #{userNick}")
    int countByUserNick(@Param("userNick") String usernick);


}
