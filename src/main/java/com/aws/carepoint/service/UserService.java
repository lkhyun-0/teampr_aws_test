package com.aws.carepoint.service;

import com.aws.carepoint.dto.UsersDto;
import com.aws.carepoint.mapper.UserMapper;
import com.aws.carepoint.util.RandomPassword;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

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

    public UsersDto processKakaoLogin(Map<String, Object> kakaoUser, HttpSession session) {
        try {
            // ✅ 1. JSON 형식으로 변환 후 콘솔 출력
            ObjectMapper objectMapper = new ObjectMapper();
            String kakaoUserJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(kakaoUser);
            System.out.println("📌 카카오에서 받은 사용자 정보 (JSON): \n" + kakaoUserJson);
        } catch (Exception e) {
            System.out.println("🚨 JSON 변환 중 오류 발생: " + e.getMessage());
        }

        // ✅ 2. 카카오에서 받은 사용자 정보 파싱
        String kakaoId = kakaoUser.get("id").toString();
        Map<String, Object> properties = (Map<String, Object>) kakaoUser.get("properties");
        Map<String, Object> kakaoAccount = (Map<String, Object>) kakaoUser.get("kakao_account");

        // ✅ 3. 필요한 데이터 추출
        String nickname = properties.getOrDefault("nickname", "No Nickname").toString();  // 닉네임
        String email = (kakaoAccount != null && kakaoAccount.containsKey("email"))
                ? kakaoAccount.get("email").toString()
                : "no-email";  // 이메일
        String phone = (kakaoAccount != null && kakaoAccount.containsKey("phone_number"))
                ? kakaoAccount.get("phone_number").toString()
                : "no-phone";  // 전화번호
        String randomPwd = RandomPassword.generateRandomPassword();  // ✅ 랜덤 비밀번호 생성 후 암호화

        // ✅ 4. DB에서 기존 회원 조회
        UsersDto existingUser = userMapper.findByEmail(email);

        if (existingUser == null) {
            // ✅ 5. 기존 회원이 없으면 자동 회원가입
            UsersDto newUser = new UsersDto();
            newUser.setUserId(kakaoId); // 🔥 카카오 ID를 userId로 저장
            newUser.setUserNick(nickname);
            newUser.setUserPwd(passwordEncoder.encode(randomPwd)); // 🔐 비밀번호 암호화 후 저장
            newUser.setEmail(email);
            newUser.setPhone(phone);
            newUser.setSocialLoginStatus(1); // ✅ 소셜 로그인 유저임을 표시

            userMapper.insertUser(newUser);  // 🔥 DB에 신규 회원 저장
            existingUser = newUser; // 신규 회원 정보 저장
            System.out.println("✅ 새로운 카카오 사용자 회원가입 완료! (ID: " + existingUser + ")");
        } else {
            System.out.println("🔹 기존 카카오 사용자 로그인 성공! (ID: " + existingUser + ")");
        }

        // ✅ 6. 세션에 사용자 정보 저장 (자동 로그인) 여기다 필요한 값 다 가져오면됨
        session.setAttribute("userPk", existingUser.getUserPk());
        session.setAttribute("userNick", existingUser.getUserNick());
        session.setAttribute("email", existingUser.getEmail());
        session.setAttribute("socialLoginStatus", existingUser.getSocialLoginStatus());

        //System.out.println("✅ 카카오 로그인 완료! 세션 저장 userPk: " + session.getAttribute("userPk"));

        return existingUser;
    }





}
