package com.aws.carepoint.controller;

import com.aws.carepoint.vo.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/user/")
public class UserController {

    @GetMapping("userJoin")       // 회원가입 페이지 보여주기
    public String userJoin() {
        return "user/userJoin";
    }

    @PostMapping("userJoinAction")

    public String userJoinAction(User user, RedirectAttributes rttr, HttpSession session) {

        return "redirect:/user/userJoin";
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
