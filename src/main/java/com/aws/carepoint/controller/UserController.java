package com.aws.carepoint.controller;

import com.aws.carepoint.dto.DetailDto;
import com.aws.carepoint.dto.UsersDto;
import com.aws.carepoint.mapper.DetailMapper;
import com.aws.carepoint.mapper.UserMapper;
import com.aws.carepoint.service.DetailService;
import com.aws.carepoint.service.UserService;
import com.aws.carepoint.util.RandomPassword;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public ResponseEntity<Boolean> checkNickname(@RequestParam("userNick") String userNick) {
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



    @PostMapping("doSignIn") // 일반 로그인
    public ResponseEntity<Map<String, Object>> doSignIn(
            @RequestBody Map<String, String> loginData,  // ✅ JSON 데이터 받기
            HttpSession session) {

        String userId = loginData.get("userId");
        String userPwd = loginData.get("userPwd");

        Map<String, Object> response = new HashMap<>();

        // 1. 유효성 검사 (아이디/비밀번호 입력 확인)
        if (userId == null || userPwd == null || userId.isEmpty() || userPwd.isEmpty()) {
            response.put("error", "아이디 또는 비밀번호를 입력해주세요.");
            response.put("success", false);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        UsersDto usersDto = userService.checkId(userId);
        if (usersDto != null) {
            if (userService.checkPwd(userPwd, usersDto.getUserPwd())) {

        // 5. 로그인 성공 → 세션 저장
        session.setAttribute("userPk", usersDto.getUserPk());
        session.setAttribute("authLevel", usersDto.getAuthLevel());
        session.setAttribute("socialLoginStatus", usersDto.getSocialLoginStatus());
        session.setAttribute("userName", usersDto.getUserName());
        session.setAttribute("userNick", usersDto.getUserNick());
        session.setAttribute("joinDate", usersDto.getJoinDate());
        session.setAttribute("phone", usersDto.getPhone());
        session.setAttribute("email", usersDto.getEmail());

        //System.out.println("로그인 성공! 세션 설정 userPk: " + usersDto.getUserPk()); 세션에 담긴지 확인

        // 6. 리다이렉트 URL 결정 (세션에 저장된 `saveUrl`이 있으면 해당 경로로 이동)
        String redirectUrl = (session.getAttribute("saveUrl") != null) ?
                session.getAttribute("saveUrl").toString() : "/user/mainPage";

                response.put("message", "로그인 성공");
                response.put("success", true);
                response.put("redirect", redirectUrl);
                return ResponseEntity.ok(response);
            } else {
                response.put("error", "아이디 또는 비밀번호가 잘못되었습니다.");
                response.put("success", false);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
        } else {
            response.put("error", "해당하는 아이디가 없습니다.");
            response.put("success", false);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }


    @GetMapping("userDetail")
    public String userDetail() {
        return "user/userDetail";
    }

    @PostMapping("doInsertDetail")
    public ResponseEntity<Map<String, Object>> doInsertDetail(@RequestBody DetailDto detailDto, HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        try {
            //System.out.println("doInsertDetail 실행됨!");
            //System.out.println("전달된 데이터: " + detailDto);

            // 세션에서 userPk 가져오기     이거 때문에 소셜로그인은 못씀 세션에 없어서
            Integer userPk = (Integer) session.getAttribute("userPk");

            if (userPk == null) {       // 일반 로그인 한 사람이 상세정보 입력하려면 오류 !!
                System.out.println("🚨 세션에 userPk 없음! 로그인 필요");
                response.put("status", "error");
                response.put("message", "로그인이 필요합니다.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            //  userPk를 DetailDto에 설정 이 값은 동일해야하는지 ?
            detailDto.setUserPk(userPk);
            detailDto.setDetailPk(userPk);

            detailService.insertDetail(detailDto);

            response.put("status", "success");
            response.put("message", "회원 상세정보 저장 완료! 메인페이지 이동 !");

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

    // ==== 세션 회원 번호 로그아웃 매핑 ====
    @GetMapping("session")     // 여기는 로그인 여부 판단하는 곳
    public ResponseEntity<Map<String, Object>> getSessionInfo(HttpSession session) {
        Object userPk = session.getAttribute("userPk"); // ✅ 로그인 정보 확인

        Map<String, Object> response = new HashMap<>();
        if (userPk != null) {

            response.put("loggedIn", true);
            response.put("userPk", userPk.toString());  // 🔥 명확하게 String으로 변환
        } else {
            response.put("loggedIn", false);
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping("kakaoSignIn")
    public ResponseEntity<Map<String, Object>> kakaoSignIn(@RequestBody UsersDto kakaoUser, HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        // 📌 전화번호로 userPk 조회 (String 타입으로 반환될 가능성 있음)
        String findUserPk = userMapper.findPhoneByPhone(kakaoUser.getPhone());

        // 🔹 String → Integer 변환 (예외 방지)
        Integer userPk = (findUserPk != null && !findUserPk.isEmpty()) ? Integer.parseInt(findUserPk) : null;

        UsersDto existingUser = userMapper.findByEmail(kakaoUser.getEmail());
        String redirectUrl;

        if (existingUser == null) {
            // ✅ 랜덤 비밀번호 생성 및 설정
            String randomPwd = RandomPassword.generateRandomPassword();
            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            kakaoUser.setUserPwd(passwordEncoder.encode(randomPwd));
            kakaoUser.setUserName(kakaoUser.getUserNick()); // userName이 없으면 userNick 사용
            kakaoUser.setSocialLoginStatus(1);

            userMapper.insertUser(kakaoUser); // 사용자 정보 삽입

            // 📌 회원정보 다시 조회
            if (kakaoUser.getUserPk() == 0) {
                existingUser = userMapper.findByEmail(kakaoUser.getEmail());
            } else {
                existingUser = kakaoUser;
            }

            session.setAttribute("detailInsert", true);
            session.setAttribute("userPk", existingUser.getUserPk());

            redirectUrl = "/user/userDetail";
        } else {
            redirectUrl = "/user/mainPage";
        }

        // 📌 조회된 userPk 값을 세션에 저장 (기존 데이터보다 우선 적용)
        if (userPk != null) {
            session.setAttribute("userPk", userPk);
        }

        // 📌 응답 메시지 추가
        response.put("message", "카카오 로그인 성공!");
        response.put("success", true);
        response.put("redirect", redirectUrl);

        return ResponseEntity.ok(response);
    }



    @GetMapping("mainPage")
    public String mainPage() {
        return "user/mainPage";
    }

    @GetMapping("selfCheckList")
    public String selfCheckList() {
        return "user/selfCheckList";
    }

    @GetMapping("logout")       // 세션에 담긴 값 삭제 초기화
    public ResponseEntity<Map<String, String>> logout(HttpSession session) {
        session.invalidate(); // ✅ 세션 삭제
        Map<String, String> response = new HashMap<>();
        response.put("message", "로그아웃되었습니다.");
        response.put("redirect", "/user/mainPage");

        return ResponseEntity.ok(response);
    }







}
