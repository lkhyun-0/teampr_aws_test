package com.aws.carepoint.controller;

import com.aws.carepoint.dto.UsersDto;
import com.aws.carepoint.mapper.UserMapper;
import com.aws.carepoint.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller // @RestController= @Controller + @ResponseBody
@RequestMapping("/user/")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper; // 🔹 userMapper 추가

    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper; // 🔹 생성자에서 주입
    }
    @ResponseBody
    @PostMapping("userSignUp")
    public ResponseEntity<Map<String, String>> userSignUp(@RequestBody UsersDto usersDto, HttpSession session) {
        try {
            userService.userSignUp(usersDto);
            System.out.println("유저디티오 확인 : " + usersDto);

            session.setAttribute("detailInsert", true);
            session.setAttribute("user_pk", usersDto.getUser_pk());

            // ✅ JSON 응답으로 변경 (기존 단순 문자열 반환 → JSON)
            return ResponseEntity.ok(Map.of("message", "회원가입 성공!", "redirect", "/user/userDetail"));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "서버 오류 발생: " + e.getMessage()));
        }
    }
    // 아이디 중복 체크 API
    @GetMapping("checkUserId")
    public ResponseEntity<Boolean> checkUserId(@RequestParam String userid) {
        boolean isDuplicate = userMapper.countByUserId(userid) > 0;
        return ResponseEntity.ok(isDuplicate);
    }

    // 닉네임 중복 체크 API
    @GetMapping("checkNickname")
    public ResponseEntity<Boolean> checkNickname(@RequestParam String usernick) {
        boolean isDuplicate = userMapper.countByUserNick(usernick) > 0;
        return ResponseEntity.ok(isDuplicate);
    }




    @GetMapping("userJoin")       // 회원가입 페이지 보여주기
    public String userJoin() {
        return "user/userJoin";
    }
    @GetMapping("userLogin")
    public String userLogin() {
        return "user/userLogin";
    }
    @GetMapping("userDetail")
    public String userDetail() {
        return "user/userDetail";
    }
    @GetMapping("myPage")
    public String myPage() {
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







}
