package com.aws.carepoint.controller;

import com.aws.carepoint.dto.DetailDto;
import com.aws.carepoint.dto.FreeDto;
import com.aws.carepoint.dto.QnaDto;
import com.aws.carepoint.dto.UsersDto;
import com.aws.carepoint.mapper.DetailMapper;
import com.aws.carepoint.mapper.FreeMapper;
import com.aws.carepoint.mapper.QnaMapper;
import com.aws.carepoint.mapper.UserMapper;
import com.aws.carepoint.service.DetailService;
import com.aws.carepoint.service.UserService;
import com.aws.carepoint.util.RandomPassword;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller  // @RestController= @Controller + @ResponseBody
@RequestMapping("/user/")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper; // 🔹 userMapper 추가
    private final DetailService detailService;
    private final DetailMapper detailMapper;
    private final FreeMapper freeMapper;
    private final QnaMapper qnaMapper;
    private final PasswordEncoder passwordEncoder;


    public UserController(UserService userService, UserMapper userMapper, DetailService detailService, DetailMapper detailMapper, FreeMapper freeMapper, QnaMapper qnaMapper, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.userMapper = userMapper; // 🔹 생성자에서 주입
        this.detailService = detailService;
        this.detailMapper = detailMapper;
        this.freeMapper = freeMapper;
        this.qnaMapper = qnaMapper;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 전화번호 정규화 함수 (010XXXXXXXX 형식) utill 로 빼야함
     */
    private String normalizePhoneNumber(String phone) {
        if (phone == null || phone.isEmpty()) {
            return null;
        }
        // 1. 국제번호 제거
        phone = phone.replaceAll("^\\+82\\s*", "0");
        // 2. 숫자 외 모든 문자 제거 (공백, 하이픈 등)
        phone = phone.replaceAll("[^0-9]", "");
        return phone;
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
            // 🔹 전화번호 정규화 적용
            String formattedPhone = normalizePhoneNumber(usersDto.getPhone());
            usersDto.setPhone(formattedPhone); // 정규화된 전화번호 설정
            userService.userSignUp(usersDto);
            //System.out.println("유저 DTO 확인: " + usersDto);

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

    @GetMapping("signIn")       // 로그인 페이지
    public String signIn() {
        return "user/signIn";
    }

    @PostMapping("doSignIn")        // 일반로그인
    public ResponseEntity<Map<String, Object>> doSignIn(
            @RequestBody Map<String, String> loginData,
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

        // 2. 회원 정보 조회
        UsersDto usersDto = userService.checkId(userId);

        if (usersDto == null) {
            response.put("error", "해당하는 아이디가 없습니다.");
            response.put("success", false);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        // **여기서 탈퇴 회원 여부를 체크**
        if (usersDto.getDelStatus() == 1) {
            response.put("error", "탈퇴한 회원입니다.");
            response.put("success", false);
            response.put("redirect", "/user/login");  // 로그인 페이지로 이동
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        // 4. 비밀번호 검사
        if (!userService.checkPwd(userPwd, usersDto.getUserPwd())) {
            response.put("error", "아이디 또는 비밀번호가 잘못되었습니다.");
            response.put("success", false);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        // 5. 로그인 성공 → 세션 저장
        session.setAttribute("userPk", usersDto.getUserPk());
        session.setAttribute("authLevel", usersDto.getAuthLevel());
        session.setAttribute("socialLoginStatus", usersDto.getSocialLoginStatus());
        session.setAttribute("userName", usersDto.getUserName());
        session.setAttribute("userNick", usersDto.getUserNick());
        session.setAttribute("joinDate", usersDto.getJoinDate());
        session.setAttribute("phone", usersDto.getPhone());
        session.setAttribute("email", usersDto.getEmail());
        session.setAttribute("del_status", usersDto.getDelStatus());

        // 세션에 저장된 redirectUrl을 가져옴
        String redirectUrl = (String) session.getAttribute("redirectUrl");
        if (redirectUrl != null) {
            session.removeAttribute("redirectUrl"); // 사용 후 제거
        } else {
            redirectUrl = "/user/mainPage"; // 기본값
        }

        response.put("message", "로그인 성공");
        response.put("success", true);
        response.put("redirect", redirectUrl);
        return ResponseEntity.ok(response);
    }

    @GetMapping("userDetail")
    public String userDetail() {
        return "user/userDetail";
    }

    @PostMapping("doInsertDetail")      // 상세정보 입력
    public ResponseEntity<Map<String, Object>> doInsertDetail(@RequestBody DetailDto detailDto, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        try {
            Integer userPk = (Integer) session.getAttribute("userPk");
            if (userPk == null) {       // 기본 회원정보 없으면 상세정보 입력불가
                response.put("status", "error");
                response.put("message", "로그인이 필요합니다.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
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


    @GetMapping("myPage")       // 마이페이지에 들어오면 보여야 할 회원정보들 주문받기
    public String myPage(HttpSession session, Model model) {
        Integer userPk = (Integer) session.getAttribute("userPk");

        if (userPk == null) {
            return "redirect:/user/signIn"; // 세션이 없으면 로그인 페이지로 이동
        }
        UsersDto userInfo = userMapper.getUserById(userPk);
        DetailDto detailDto = detailMapper.getUserDetailById(userPk);

        if (userInfo == null) {
            return "redirect:/user/signIn"; // DB에서 조회 실패하면 로그인 페이지로 이동

        }
        // 최근 5개 글 조회
        List<FreeDto> recentFree = freeMapper.getRecentFree(userPk);
        List<QnaDto> recentQna = qnaMapper.getRecentQna(userPk);


        // 모델에 사용자 정보 추가해서 마이페이지로 보내기
        model.addAttribute("userInfo", userInfo);
        model.addAttribute("detailDto", detailDto);
        model.addAttribute("recentFree", recentFree);
        model.addAttribute("recentQna", recentQna);

        return "user/myPage";
    }

    // ==== 세션에 담긴 회원 번호 초기화로 로그아웃 ====
    @GetMapping("session")     // 여기는 로그인 여부 판단하는 곳
    public ResponseEntity<Map<String, Object>> getSessionInfo(HttpSession session) {
        Object userPk = session.getAttribute("userPk");
        Map<String, Object> response = new HashMap<>();
        if (userPk != null) {
            response.put("loggedIn", true);
            response.put("userPk", userPk.toString());
        } else {
            response.put("loggedIn", false);
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("kakaoSignIn")     // 카카오로그인+회원가입
    public ResponseEntity<Map<String, Object>> kakaoSignIn(@RequestBody UsersDto kakaoUser, HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        String findUserPk = userMapper.findPhoneByPhone(kakaoUser.getPhone());

        String phone = normalizePhoneNumber(kakaoUser.getPhone());
       // System.out.println("전화번호 정규화 !! " + phone); //이거 기준으로 가져올건데 카카오 유저랑 일반유저가 다름

        Integer userPk = (findUserPk != null && !findUserPk.isEmpty()) ? Integer.parseInt(findUserPk) : null;

        UsersDto existingUser = userMapper.findByEmail(kakaoUser.getEmail());
        String redirectUrl;

        if (existingUser == null) {
            // 랜덤 비밀번호 생성 및 설정
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

        if (userPk != null) {
            session.setAttribute("userPk", userPk);
        }

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

    @PostMapping("findPassword")        // 비번 찾기
    public ResponseEntity<?> findPassword(@RequestBody Map<String, String> request) {
        System.out.println("📌 받은 데이터: " + request); // 요청 데이터 출력
        String userName = request.get("userName");
        String userId = request.get("userId");
        String phone = request.get("phone");
        boolean isSuccess = userService.resetPasswordAndSendSMS(userName, userId, phone);
        if (isSuccess) {
            return ResponseEntity.ok(Map.of("success", true, "message", "임시 비밀번호가 문자로 전송되었습니다."));
        } else {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "일치하는 회원 정보를 찾을 수 없습니다."));
        }
    }

    @PostMapping("deleteUser")      // 회원 탈퇴
    public ResponseEntity<Map<String, Object>> deleteUser(HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        // 1. 현재 로그인한 사용자의 userPk 가져오기
        Integer userPk = (Integer) session.getAttribute("userPk");

        if (userPk == null) {
            response.put("error", "로그인이 필요합니다.");
            response.put("success", false);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        // 2. 회원 정보 조회
        UsersDto usersDto = userService.checkUserByPk(userPk);
        if (usersDto == null) {
            response.put("error", "해당하는 회원이 없습니다.");
            response.put("success", false);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        // 3. 회원 상태를 '탈퇴' 상태로 변경
        boolean isDeleted = userService.markUserAsDeleted(userPk);
        if (isDeleted) {
            // 4. 세션 삭제 (로그아웃 효과)
            session.invalidate();

            response.put("message", "회원 탈퇴가 완료되었습니다.");
            response.put("success", true);
            response.put("redirect", "/user/signIn");  // 탈퇴 후 로그인 페이지로 이동
            return ResponseEntity.ok(response);
        } else {
            response.put("error", "회원 탈퇴에 실패했습니다.");
            response.put("success", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("modifyUserPwd")
    public ResponseEntity<?> modifyUserPwd(@RequestBody Map<String, String> request) {
        // ✅ 요청에서 userPk 가져오기 (세션에서 가져올 필요 없음)
        String userPkStr = request.get("userPk");
        String newPwd = request.get("newPassword");

        if (userPkStr == null || userPkStr.trim().isEmpty()) {
            //log.error("비밀번호 변경 실패: userPk 값이 없음");
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "회원 정보가 없습니다."));
        }

        int userPk;
        try {
            userPk = Integer.parseInt(userPkStr);
        } catch (NumberFormatException e) {
            //log.error("비밀번호 변경 실패: userPk 변환 오류 - {}", userPkStr);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("success", false, "message", "잘못된 회원 정보입니다."));
        }

        if (newPwd == null || newPwd.trim().isEmpty()) {
            //log.error("비밀번호 변경 실패: 새로운 비밀번호 없음");
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "비밀번호를 입력하세요."));
        }

        //log.info("비밀번호 변경 요청 - UserPK: {}, 입력된 비밀번호: {}", userPk, newPwd);

        // ✅ 새 비밀번호 암호화 후 저장
        String encodedPwd = passwordEncoder.encode(newPwd);
        //log.info("비밀번호 암호화 완료 - UserPK: {}", userPk);

        userService.modifyUserPwd(userPk, encodedPwd);
        //log.info("비밀번호 변경 성공 - UserPK: {}", userPk);

        return ResponseEntity.ok(Map.of("success", true));
    }











}
