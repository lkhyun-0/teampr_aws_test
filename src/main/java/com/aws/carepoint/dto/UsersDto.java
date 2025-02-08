package com.aws.carepoint.dto;
import lombok.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@Setter
@Getter
public class UsersDto {
    private int userPk;
    private int authLevel = 3; // 기본값 설정
    private int socialLoginStatus = 0; // 기본값 설정
    @NotBlank(message = "아이디는 필수 입력값입니다.")
    private String userId;
    @NotBlank(message = "이름은 필수 입력값입니다.")
    private String userName;
    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
    private String userPwd;
    @NotBlank(message = "닉네임은 필수 입력값입니다.")
    private String userNick;
    @NotBlank(message = "전화번호는 필수 입력값입니다.")
    private String phone;
    @NotBlank(message = "이메일은 필수 입력값입니다.")
    @Email(message = "올바른 이메일 형식을 입력해주세요.")
    private String email;
    private LocalDateTime joinDate;
    private LocalDateTime updateDate;
    private LocalDateTime delDate;
    private int delStatus = 0; // 기본값 설정

}
