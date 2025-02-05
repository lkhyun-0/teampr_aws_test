package com.aws.carepoint.mapper;

import com.aws.carepoint.dto.UsersDto;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface UserMapper {
    // 회원 가입
    @Insert("INSERT INTO users (userid, username, userpwd, usernick, phone, email, auth_level, social_login_status, del_status) " +
            "VALUES (#{userid}, #{username}, #{userpwd}, #{usernick}, #{phone}, #{email}, #{auth_level}, #{social_login_status}, #{del_status})")
    void insertUser(UsersDto usersDto);

    // 아이디 중복 체크
    @Select("SELECT COUNT(*) FROM users WHERE userid = #{userid}")
    int countByUserId(String userid);

    // 닉네임 중복 체크
    @Select("SELECT COUNT(*) FROM users WHERE usernick = #{usernick}")
    int countByUserNick(String usernick);

}
