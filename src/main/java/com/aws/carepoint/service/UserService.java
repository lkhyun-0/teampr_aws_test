package com.aws.carepoint.service;

import com.aws.carepoint.dto.UsersDto;
import com.aws.carepoint.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
@Slf4j
@Service
public class UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public void userSignUp(UsersDto usersDto) {

        // 기본값 설정
        usersDto.setAuth_level(3); // 일반 회원
        usersDto.setSocial_login_status(0); // ✅ setter 메서드 사용
        usersDto.setDel_status(0); // 기본값: 탈퇴하지 않은 상태
        // 비밀번호 암호화
        String encodedPwd = passwordEncoder.encode(usersDto.getUserpwd());
        usersDto.setUserpwd(encodedPwd);
        System.out.println("암호화된 비번: " + encodedPwd);

        // 회원 정보 저장
        userMapper.insertUser(usersDto);
    }

}
