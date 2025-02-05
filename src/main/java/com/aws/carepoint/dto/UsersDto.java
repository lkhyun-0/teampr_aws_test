package com.aws.carepoint.dto;


import lombok.*;

@Data
public class UsersDto {

    private int user_pk;
    private int auth_level;
    private int social_login_status;
    private String userid;
    private String username;
    private String userpwd;
    private String usernick;
    private String phone;
    private String email;
    private String join_date;
    private String update_date;
    private int del_status;
    private String delDate;
}
