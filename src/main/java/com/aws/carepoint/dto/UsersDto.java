package com.aws.carepoint.dto;
import lombok.*;
@Data
public class UsersDto {
    private int user_pk;
    private int auth_level = 3; // 기본값 설정
    private int social_login_status = 0; //기본값 설정
    private String userid;
    private String username;
    private String userpwd;
    private String usernick;
    private String phone;
    private String email;
    private String join_date;
    private String update_date;
    private int del_status = 0; // 기본값 설정
    private String delDate;


}
