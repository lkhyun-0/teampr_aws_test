package com.aws.carepoint.dto;
import lombok.*;

import java.time.LocalDateTime;

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
    private LocalDateTime joinDate;
    private LocalDateTime updateDate;
    private LocalDateTime delDate;
    private int delStatus = 0; // 기본값 설정

}
