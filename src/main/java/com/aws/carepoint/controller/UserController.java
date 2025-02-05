package com.aws.carepoint.controller;

import com.aws.carepoint.dto.UsersDto;
import com.aws.carepoint.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller // @RestController= @Controller + @ResponseBody
@RequestMapping("/user/")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }
    @ResponseBody
    @PostMapping("userSignUp")
    public ResponseEntity<String> userSignUp(@RequestBody UsersDto usersDto) {


        userService.userSignUp(usersDto);   // dto 담아서 서비스메서드 호출하기

        System.out.println("회원가입 데이터: " + usersDto);
        return ResponseEntity.ok("성공메세지 : 회원가입 성공 !!");

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
