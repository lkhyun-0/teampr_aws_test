package com.aws.carepoint.controller;

import com.aws.carepoint.dto.UsersDto;
import com.aws.carepoint.mapper.UserMapper;
import com.aws.carepoint.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<String> userSignUp(@RequestBody UsersDto usersDto, HttpSession session) {
        userService.userSignUp(usersDto);   // 회원가입 처리 !

        session.setAttribute("detailInsert", true);
        session.setAttribute("user_pk", usersDto.getUser_pk());
        // 상세정보 입력할 때 사용할 회원번호 세션에 담기

        return ResponseEntity.ok("회원가입 성공! 상세 정보를 입력해주세요.");

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
