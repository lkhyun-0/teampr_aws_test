package com.aws.carepoint.mapper;

import com.aws.carepoint.dto.UsersDto;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface UserMapper {
    // 회원 가입
    @Insert("INSERT INTO users (userid, username, userpwd, usernick, phone, email, auth_level, social_login_status, del_status) " +
            "VALUES (#{userId}, #{userName}, #{userPwd}, #{userNick}, #{phone}, #{email}, #{authLevel}, #{socialLoginStatus}, #{delStatus})")
    @Options(useGeneratedKeys = true, keyProperty = "user_pk")  // ✅ 자동 증가된 user_pk 값을 DTO에 반영
    void insertUser(UsersDto usersDto);

    // 아이디 중복 체크
    @Select("SELECT COUNT(*) FROM users WHERE userid = #{userId}")
    int countByUserId(String userid);       // 중복체크 오류 해결해야함 todo

    // 닉네임 중복 체크
    @Select("SELECT COUNT(*) FROM users WHERE usernick = #{userNick}")
    int countByUserNick(String usernick);

}
