package com.aws.carepoint.service;

import com.aws.carepoint.dto.UsersDto;
import com.aws.carepoint.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
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

    public void userSignUp(UsersDto usersDto) {     // 회원가입
        // 아이디 중복 검사
        if (userMapper.countByUserId(usersDto.getUserId()) > 0) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다. 다른 아이디를 입력해주세요.");
        }
        // 닉네임 중복 검사
        if (userMapper.countByUserNick(usersDto.getUserNick()) > 0) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다. 다른 닉네임을 입력해주세요.");
        }
        // 비밀번호 암호화
        String encodedPwd = passwordEncoder.encode(usersDto.getUserPwd());
        usersDto.setUserPwd(encodedPwd);
        //System.out.println("암호화된 비번: " + encodedPwd);
        // 회원 정보 저장
        userMapper.insertUser(usersDto);
        System.out.println("회원가입 DB 저장 완료!");
    }

    // 로그인
    public UsersDto checkId(String userId) {
        UsersDto usersDto = userMapper.findByUserId(userId);
        if (usersDto != null) {
            System.out.println("DB에서 가져온 userPk: " + usersDto.getUserPk());
        } else {
            System.out.println("DB에서 해당 userId를 찾을 수 없음");
        }
        return userMapper.findByUserId(userId); // DB에서 사용자 정보 조회
    }

    /**
     * 🔹 비밀번호 검증 메서드
     * - 입력된 비밀번호와 DB에 저장된 비밀번호 비교
     * - 비밀번호는 BCrypt로 암호화되어 저장되어 있음
     */
    public boolean checkPwd(String rawPwd, String encodedPwd) {//(사용자가 입력한 비번,암호화된 비번 대조하기)
        return passwordEncoder.matches(rawPwd, encodedPwd);
    }


}
