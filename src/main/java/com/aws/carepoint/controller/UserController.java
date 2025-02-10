package com.aws.carepoint.controller;

import com.aws.carepoint.dto.DetailDto;
import com.aws.carepoint.dto.UsersDto;
import com.aws.carepoint.mapper.DetailMapper;
import com.aws.carepoint.mapper.UserMapper;
import com.aws.carepoint.service.DetailService;
import com.aws.carepoint.service.KakaoAuthService;
import com.aws.carepoint.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
    private final KakaoAuthService kakaoAuthService;


    @Value("${kakao.client-id}") // application.yml에서 가져옴
    private String kakaoClientId;

    @Value("${kakao.redirect-uri}")
    private String kakaoRedirectUri;


    public UserController(UserService userService, UserMapper userMapper, DetailService detailService, DetailMapper detailMapper, KakaoAuthService kakaoAuthService) {
        this.userService = userService;
        this.userMapper = userMapper; // 🔹 생성자에서 주입
        this.detailService = detailService;
        this.detailMapper = detailMapper;
        this.kakaoAuthService = kakaoAuthService;
    }




    // 카카오 로그인 URL 반환
    @GetMapping("kakao/login")
    public String kakaoLogin(Model model) {
        String kakaoUrl = "https://kauth.kakao.com/oauth/authorize" +
                "?client_id=" + kakaoClientId +
                "&redirect_uri=" + kakaoRedirectUri +
                "&response_type=code";
        model.addAttribute("kakaoUrl", kakaoUrl);
        return "user/login"; // 로그인 페이지로 이동 (Thymeleaf에서 버튼 클릭 시 호출)
    }
    @GetMapping("kakao/callback")
    public String kakaoCallback(@RequestParam("code") String code, HttpSession session) {
        System.out.println("📢 카카오 로그인 성공! 인증 코드: " + code);

        // ✅ 1. 카카오 액세스 토큰 요청
        String accessToken = kakaoAuthService.getKakaoAccessToken(code);
        System.out.println("📢 받은 액세스 토큰: " + accessToken);

        // ✅ 2. 카카오 사용자 정보 가져오기
        Map<String, Object> kakaoUser = kakaoAuthService.getUserInfo(accessToken);
        System.out.println("📢 카카오 사용자 정보: " + kakaoUser);

        // ✅ 3. 회원 가입 또는 로그인 처리
        UsersDto usersDto = userService.processKakaoLogin(kakaoUser, session);

        // ✅ 4. 신규 회원이면 추가 정보 입력 페이지로 이동, 기존 회원이면 마이페이지로 이동
        if (usersDto.getSocialLoginStatus() == 1 && usersDto.getPhone() == null) {
            return "redirect:/user/userDetail"; // 🔹 신규 회원 → 상세정보 입력 페이지로 이동
        } else {
            return "redirect:/user/myPage"; // 🔹 기존 회원 → 마이페이지로 이동
        }
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
            session.setAttribute("user_pk", usersDto.getUserPk());

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
    @PostMapping("doSignIn")
    public ResponseEntity<Map<String, Object>> doSignIn(
            @RequestBody Map<String, String> loginData, HttpSession session) {

        Map<String, Object> response = new HashMap<>();

        // 🔹 로그인 데이터 유효성 검사
        String userId = loginData.get("userId");
        String userPwd = loginData.get("userPwd");

        if (userId == null || userPwd == null || userId.isEmpty() || userPwd.isEmpty()) {
            response.put("error", "아이디 또는 비밀번호를 입력해주세요.");
            response.put("success", false);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        // 🔹 DB에서 사용자 정보 조회
        UsersDto usersDto = userService.checkId(userId);
        if (usersDto != null) {
            if (userService.checkPwd(userPwd, usersDto.getUserPwd())) {
                // ✅ 세션 저장
                session.setAttribute("userPk", usersDto.getUserPk());
                session.setAttribute("authLevel", usersDto.getAuthLevel());
                session.setAttribute("userName", usersDto.getUserName());
                session.setAttribute("userNick", usersDto.getUserNick());
                session.setAttribute("joinDate", usersDto.getJoinDate());
                session.setAttribute("phone", usersDto.getPhone());
                session.setAttribute("email", usersDto.getEmail());

                // ✅ 디버깅 로그 추가
                System.out.println("✅ 로그인 성공! 세션 설정 userPk: " + usersDto.getUserPk());

                // ✅ 리다이렉트 URL 설정 (기존 `saveUrl`이 있다면 사용)
                String redirectUrl = (session.getAttribute("saveUrl") != null) ?
                        session.getAttribute("saveUrl").toString() : "/user/mainPage";

                // 🔹 JSON 응답 반환 (redirect 사용 X)
                response.put("message", "로그인 성공");
                response.put("success", true);
                response.put("redirect", redirectUrl);
                return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(response);
            } else {
                response.put("error", "아이디 또는 비밀번호가 잘못되었습니다.");
                response.put("success", false);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).contentType(MediaType.APPLICATION_JSON).body(response);
            }
        } else {
            response.put("error", "해당하는 아이디가 없습니다.");
            response.put("success", false);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).contentType(MediaType.APPLICATION_JSON).body(response);
        }
    }



    @GetMapping("userDetail")
    public String userDetail() {
        return "user/userDetail";
    }

    @PostMapping("doInsertDetail")
    public ResponseEntity<Map<String, Object>> doInsertDetail(@RequestBody DetailDto detailDto) {
        Map<String, Object> response = new HashMap<>();

        try {
            System.out.println("📢 doInsertDetail 실행됨!");
            System.out.println("📢 전달된 데이터: " + detailDto); // ✅ gender 값 확인

            // ✅ gender 값이 정상적으로 들어왔는지 확인
            if (detailDto.getGender() == null || detailDto.getGender().isEmpty()) {
                throw new IllegalArgumentException("성별 정보가 누락되었습니다.");
            }

            // ✅ 기본값 설정
            detailDto.setRegDate(LocalDateTime.now());
            detailDto.setUpdateDate(LocalDateTime.now());

            if (detailDto.getTargetCount() == null) {
                detailDto.setTargetCount(0);
            }
            if (detailDto.getSmoke() == null) {
                detailDto.setSmoke(0);
            }
            if (detailDto.getDrink() == null) {
                detailDto.setDrink(0);
            }

            // ✅ 저장 실행
            detailService.insertDetail(detailDto);

            response.put("status", "success");
            response.put("message", "회원 상세정보 저장 완료!");

        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "저장 실패: " + e.getMessage());
            e.printStackTrace();
        }

        return ResponseEntity.ok(response);
    }






    @GetMapping("myPage")
    public String myPage(HttpSession session, Model model) {
        Integer userPk = (Integer) session.getAttribute("userPk");

        // ✅ 세션 값이 제대로 저장되었는지 로그 확인
        System.out.println("📢 [DEBUG] 마이페이지 접근 userPk: " + userPk);

        if (userPk == null) {
            return "redirect:/user/signIn"; // 세션이 없으면 로그인 페이지로 이동
        }

        // ✅ 사용자 기본 정보 가져오기
        UsersDto userInfo = userMapper.getUserById(userPk);
        System.out.println("📢 [DEBUG] 조회된 사용자 정보: " + userInfo);

        // ✅ 사용자 추가 정보 (키, 체중, 흡연 등) 가져오기
        DetailDto detailDto = detailMapper.getUserDetailById(userPk);
        if (detailDto == null) {
            System.out.println("❌ [ERROR] userPk " + userPk + "에 대한 상세 정보가 없습니다. 기본값을 설정합니다.");
            detailDto = new DetailDto();
            detailDto.setUserPk(userPk);
        }


        // ✅ 모델에 데이터 추가
        model.addAttribute("userInfo", userInfo);
        model.addAttribute("detailDto", detailDto);

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
        // 세션 제거
        session.invalidate();

        // JSON 응답 반환
        Map<String, String> response = new HashMap<>();
        response.put("message", "로그아웃되었습니다.");
        response.put("redirect", "/user/mainPage"); // 🔥 로그아웃 후 리다이렉트할 페이지

        return ResponseEntity.ok(response);
    }

    @GetMapping("getUserPk")
    public ResponseEntity<Map<String, Object>> getUserPk(HttpSession session) {
        Integer userPk = (Integer) session.getAttribute("user_pk");

        if (userPk == null) {
            System.out.println("🚨 userPk 없음: 로그인 필요");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "로그인 필요"));
        }

        System.out.println("✅ 로그인된 userPk: " + userPk);
        return ResponseEntity.ok(Map.of("userPk", userPk));
    }





}
