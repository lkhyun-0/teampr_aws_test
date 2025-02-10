package com.aws.carepoint.controller;

import com.aws.carepoint.dto.DetailDto;
import com.aws.carepoint.dto.UsersDto;
import com.aws.carepoint.mapper.DetailMapper;
import com.aws.carepoint.mapper.UserMapper;
import com.aws.carepoint.service.DetailService;
import com.aws.carepoint.service.KakaoAuthService;
import com.aws.carepoint.service.UserService;
import com.aws.carepoint.util.RandomPassword;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Controller  // @RestController= @Controller + @ResponseBody
@RequestMapping("/user/")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper; // 🔹 userMapper 추가
    private final DetailService detailService;
    private final DetailMapper detailMapper;

    public UserController(UserService userService, UserMapper userMapper, DetailService detailService, DetailMapper detailMapper) {
        this.userService = userService;
        this.userMapper = userMapper; // 🔹 생성자에서 주입
        this.detailService = detailService;
        this.detailMapper = detailMapper;
    }

    @GetMapping("signUp")       // 회원가입 페이지
    public String signUp() {
        return "user/signUp";
    }

    // 아이디 중복 체크
    @GetMapping("checkUserId")
    public ResponseEntity<Boolean> checkUserId(@RequestParam("userId") String userId) {
        boolean isDuplicate = userMapper.countByUserId(userId) > 0;
        return ResponseEntity.ok(isDuplicate);
    }
    // 닉네임 중복 체크
    @GetMapping("checkNickname")
    public ResponseEntity<Boolean> checkNickname(@RequestParam String userNick) {
        boolean isDuplicate = userMapper.countByUserNick(userNick) > 0;
        return ResponseEntity.ok(isDuplicate);
    }



    @ResponseBody
    @PostMapping("dosignUp")        // 회원가입 동작
    public ResponseEntity<Map<String, String>> signUp(@Valid @RequestBody UsersDto usersDto,
                                                      BindingResult result, HttpSession session) {
        // ✅ 유효성 검사 오류가 있으면 오류 메시지 반환
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", result.getFieldError().getDefaultMessage()
            ));
        }

        try {
            userService.userSignUp(usersDto);
            System.out.println("유저 DTO 확인: " + usersDto);

            session.setAttribute("detailInsert", true);
            session.setAttribute("userPk", usersDto.getUserPk());

            return ResponseEntity.ok(Map.of(
                    "message", "회원가입 성공! 상세정보를 입력해주세요 !",
                    "redirect", "/user/userDetail"
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "서버 오류 발생: " + e.getMessage()));
        }
    }


    // ========= 회원가입 동작 완성 0206 ===============

    @GetMapping("signIn")       // 로그인 페이지
    public String signIn() {
        return "user/signIn";
    }

    @PostMapping("doSignIn") // 일반 로그인 + 소셜로그인 동작
    public ResponseEntity<Map<String, Object>> doSignIn(
            @RequestBody Map<String, String> loginData, // ✅ JSON 데이터 받기
            HttpSession session) {

        String userId = loginData.get("userId");
        String userPwd = loginData.get("userPwd");

        Map<String, Object> response = new HashMap<>();

        // ✅ 1. 유효성 검사 (아이디/비밀번호 입력 확인)
        if (userId == null || userPwd == null || userId.isEmpty() || userPwd.isEmpty()) {
            response.put("error", "아이디 또는 비밀번호를 입력해주세요.");
            response.put("success", false);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        // ✅ 2. 사용자 정보 조회
        UsersDto usersDto = userService.checkId(userId);
        if (usersDto == null) {
            response.put("error", "해당하는 아이디가 없습니다.");
            response.put("success", false);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        // ✅ 4. 비밀번호 검증
        if (!userService.checkPwd(userPwd, usersDto.getUserPwd())) {
            response.put("error", "아이디 또는 비밀번호가 잘못되었습니다.");
            response.put("success", false);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        // ✅ 5. 로그인 성공 → 세션 저장
        session.setAttribute("userPk", usersDto.getUserPk());
        session.setAttribute("authLevel", usersDto.getAuthLevel());
        session.setAttribute("socialLoginStatus", usersDto.getSocialLoginStatus());
        session.setAttribute("userName", usersDto.getUserName());
        session.setAttribute("userNick", usersDto.getUserNick());
        session.setAttribute("joinDate", usersDto.getJoinDate());
        session.setAttribute("phone", usersDto.getPhone());
        session.setAttribute("email", usersDto.getEmail());

        System.out.println("✅ 로그인 성공! 세션 설정 userPk: " + usersDto.getUserPk());

        // ✅ 6. 리다이렉트 URL 결정 (세션에 저장된 `saveUrl`이 있으면 해당 경로로 이동)
        String redirectUrl = (session.getAttribute("saveUrl") != null) ?
                session.getAttribute("saveUrl").toString() : "/user/mainPage";

        response.put("message", "로그인 성공");
        response.put("success", true);
        response.put("redirect", redirectUrl);
        return ResponseEntity.ok(response);
    }



    @GetMapping("userDetail")
    public String userDetail() {
        return "user/userDetail";
    }

    @PostMapping("doInsertDetail")
    public ResponseEntity<Map<String, Object>> doInsertDetail(@RequestBody DetailDto detailDto, HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        try {
            System.out.println("📢 doInsertDetail 실행됨!");
            System.out.println("📢 전달된 데이터: " + detailDto);

            // ✅ 세션에서 userPk 가져오기
            Integer userPk = (Integer) session.getAttribute("user_pk");

            if (userPk == null) {
                System.out.println("🚨 세션에 userPk 없음! 로그인 필요");
                response.put("status", "error");
                response.put("message", "로그인이 필요합니다.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            // ✅ userPk를 DetailDto에 설정
            detailDto.setUserPk(userPk);
            System.out.println("✅ 세션에서 가져온 userPk 설정 완료: " + userPk);

            // 기본값 처리
            detailDto.setRegDate(LocalDateTime.now());
            detailDto.setUpdateDate(LocalDateTime.now());

            if (detailDto.getTargetCount() == null) {
                detailDto.setTargetCount(0);
            }
            if (detailDto.getSmoke() == null) {
                detailDto.setSmoke(0); // 기본값: 비흡연
            }
            if (detailDto.getDrink() == null) {
                detailDto.setDrink(0); // 기본값: 음주 안 함
            }

            // ✅ 상세정보 DB 저장
            detailService.insertDetail(detailDto);

            response.put("status", "success");
            response.put("message", "회원 상세정보 저장 완료! 메인페이지 이동 !");
            System.out.println("✅ 상세정보 저장 완료!");

        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "저장 실패: " + e.getMessage());
            e.printStackTrace(); // 백엔드 콘솔에서 오류 확인
        }

        return ResponseEntity.ok(response);
    }






    @GetMapping("myPage")
    public String myPage(HttpSession session, Model model) {
        Integer userPk = (Integer) session.getAttribute("userPk");

        // 세션 값이 제대로 저장되었는지 로그 확인
        // System.out.println("마이페이지 접근 userPk: " + userPk);

        if (userPk == null) {
            return "redirect:/user/signIn"; // 세션이 없으면 로그인 페이지로 이동
        }

        UsersDto userInfo = userMapper.getUserById(userPk);
        // ✅ 사용자 추가 정보 (키, 체중 등) 가져오기
        DetailDto detailDto = detailMapper.getUserDetailById(userPk);
        System.out.println("조회된 사용자 추가 정보: " + detailDto);

        // ✅ DB에서 사용자 정보를 제대로 가져오는지 확인
        System.out.println("조회된 사용자 정보: " + userInfo);

        if (userInfo == null) {
            return "redirect:/user/signIn"; // DB에서 조회 실패하면 로그인 페이지로 이동
        }

        // ✅ 모델에 사용자 정보 추가
        model.addAttribute("userInfo", userInfo);
        model.addAttribute("detailDto", detailDto); // 여기 추가!

        return "user/myPage";
    }

    @GetMapping("mainPage")
    public String mainPage() {
        return "user/mainPage";
    }

    @GetMapping("selfCheckList")
    public String selfCheckList() {
        return "user/selfCheckList";
    }

    // ==== 세션 회원 번호 로그아웃 매핑 ====
    @GetMapping("session")     //
    public ResponseEntity<Map<String, Object>> getSessionInfo(HttpSession session) {
        Object userPk = session.getAttribute("userPk"); // ✅ 로그인 정보 확인

        Map<String, Object> response = new HashMap<>();
        if (userPk != null) {

            response.put("loggedIn", true);
            response.put("userPk", userPk.toString());  // 🔥 명확하게 String으로 변환
            System.out.println("✅ 현재 로그인된 사용자 Pk: " + userPk);
        } else {
            System.out.println("❌ 세션에 로그인 정보 없음");
            response.put("loggedIn", false);
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("logout")
    public ResponseEntity<Map<String, String>> logout(HttpSession session) {
        session.invalidate(); // ✅ 세션 삭제
        Map<String, String> response = new HashMap<>();
        response.put("message", "로그아웃되었습니다.");
        response.put("redirect", "/user/mainPage"); // 🚀 메인 페이지로 이동

        return ResponseEntity.ok(response);
    }

    @GetMapping("getUserPk")
    public ResponseEntity<Map<String, Object>> getUserPk(HttpSession session) {
        Integer userPk = (Integer) session.getAttribute("userPk");

        if (userPk == null) {
            System.out.println("🚨 userPk 없음: 로그인 필요");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "로그인 필요"));
        }

        System.out.println("✅ 로그인된 userPk: " + userPk);
        return ResponseEntity.ok(Map.of("userPk", userPk));
    }



    @PostMapping("kakaoSignIn")
    public ResponseEntity<Map<String, Object>> kakaoSignIn(@RequestBody UsersDto kakaoUser, HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        System.out.println("📢 카카오 로그인 요청 데이터: " + kakaoUser);
        //System.out.println("✅ 현재 세션 ID (로그인 전): " + session.getId());

        UsersDto existingUser = userMapper.findByEmail(kakaoUser.getEmail());
        String redirectUrl;

        if (existingUser == null) {
            // ✅ 랜덤 비밀번호 생성 및 설정
            String randomPwd = RandomPassword.generateRandomPassword();
            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            kakaoUser.setUserPwd(passwordEncoder.encode(randomPwd));
            kakaoUser.setUserName(kakaoUser.getUserNick()); // ✅ userName이 없으면 userNick 사용
            kakaoUser.setSocialLoginStatus(1);


            userMapper.insertUser(kakaoUser); // 🔥 DB에 저장 (userPk 자동 생성됨)



            System.out.println("✅ DB 저장 후 userPk: " + kakaoUser.getUserPk()); // 🔥 userPk 확인

            if (kakaoUser.getUserPk() == 0) {
                // 🔥 userPk가 0이면 다시 조회
                existingUser = userMapper.findByEmail(kakaoUser.getEmail());
            } else {
                existingUser = kakaoUser;
            }

            System.out.println("✅ 새로운 카카오 사용자 회원가입 완료! (ID: " + existingUser.getUserPk() + ")");

            session.setAttribute("detailInsert", true);
            session.setAttribute("userPk", existingUser.getUserPk());
            redirectUrl = "/user/userDetail";
        } else {
            redirectUrl = "/user/mainPage";
        }

        // ✅ 세션 저장
        session.setAttribute("userPk", existingUser.getUserPk());
        session.setAttribute("userNick", existingUser.getUserNick());
        session.setAttribute("email", existingUser.getEmail());
        session.setAttribute("userName", existingUser.getUserName());

        //System.out.println("✅ 현재 세션 ID (로그인 후): " + session.getId());
        System.out.println("✅ 현재 로그인된 userPk: " + existingUser.getUserPk());

        response.put("message", "카카오 로그인 성공!");
        response.put("success", true);
        response.put("redirect", redirectUrl);
        return ResponseEntity.ok(response);
    }






}
