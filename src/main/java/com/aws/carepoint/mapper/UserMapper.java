package com.aws.carepoint.mapper;

import com.aws.carepoint.dto.UsersDto;
import org.apache.ibatis.annotations.*;
@Mapper
public interface UserMapper {

    // ✅ 회원 가입 (ResultMap이 필요 없음)
    @Insert("INSERT INTO users (userid, username, userpwd, usernick, phone, email, auth_level, social_login_status, del_status, delDate) " +
            "VALUES (#{userId}, #{userName}, #{userPwd}, #{userNick}, #{phone}, #{email}, #{authLevel}, #{socialLoginStatus}, #{delStatus}, #{delDate})")
    @Options(useGeneratedKeys = true, keyProperty = "userPk", keyColumn = "user_pk")
    void insertUser(UsersDto usersDto);

    // ✅ 아이디 중복 체크 (단일 값 조회이므로 ResultMap 필요 없음)
    @Select("SELECT COUNT(*) FROM users WHERE userid = #{userId}")
    int countByUserId(@Param("userId") String userId);

    // ✅ 닉네임 중복 체크 (단일 값 조회이므로 ResultMap 필요 없음)
    @Select("SELECT COUNT(*) FROM users WHERE usernick = #{userNick}")
    int countByUserNick(@Param("userNick") String userNick);

    // ✅ `@Results(id = "userResultMap")`을 여기에 선언하여 한 번만 등록
    @Results(id = "userResultMap", value = {
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
            @Result(property = "delDate", column = "delDate")
    })
    @Select("SELECT user_pk, userid, username, userpwd, usernick, phone, email, auth_level, social_login_status, del_status, delDate FROM users WHERE userid = #{userId}")
    UsersDto findByUserId(@Param("userId") String userId);

    // ✅ `@ResultMap("userResultMap")`을 사용하여 중복 제거
    @Select("SELECT * FROM users WHERE user_pk = #{userPk}")
    @ResultMap("userResultMap")
    UsersDto getUserById(int userPk);
}
