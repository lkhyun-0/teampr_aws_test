package com.aws.carepoint.service;

import com.aws.carepoint.repository.UserRepository;
import com.aws.carepoint.vo.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // 회원가입 - 비밀번호 암호화 후 저장
    @Transactional
    public Users registerUser(Users users) {
        String encryptedPassword = passwordEncoder.encode(users.getUserpwd()); // 비밀번호 암호화 적용
        users.setUserpwd(encryptedPassword); // 암호화된 비밀번호 설정

        return userRepository.save(users); // DB에 저장
    }

    // 로그인 - 비밀번호 검증
    public boolean login(String userid, String rawPassword) {
        Users users = userRepository.findByUserid(userid)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        return passwordEncoder.matches(rawPassword, users.getUserpwd()); // 입력한 비밀번호와 DB 비밀번호 비교
    }
}
