package com.aws.carepoint.dto;
import lombok.*;
@Data
public class UsersDto {
    private int userPk;
    private int authLevel = 3; // 기본값 설정
    private int socialLoginStatus = 0; //기본값 설정
    private String userId;
    private String userName;
    private String userPwd;
    private String userNick;
    private String phone;
    private String email;
    private String joinDate;
    private String updateDate;
    private int delStatus = 0; // 기본값 설정
    private String delDate;
}
