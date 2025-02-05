package com.aws.carepoint.mapper;

import com.aws.carepoint.dto.UsersDto;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    // 회원 가입

    @Insert("INSERT INTO users (userid, username, userpwd, usernick, phone, email, auth_level, social_login_status, del_status) " +
            "VALUES (#{userid}, #{username}, #{userpwd}, #{usernick}, #{phone}, #{email}, #{auth_level}, #{social_login_status}, #{del_status})")
    void insertUser(UsersDto usersDto);

}
