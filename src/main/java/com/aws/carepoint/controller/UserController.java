package com.aws.carepoint.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class UserController {

    @RequestMapping("/user/userLogin")
    public String userLogin() {
        return "user/userLogin";
    }
    @RequestMapping("/user/userJoin")
    public String userJoin() {
        return "user/userJoin";
    }
    @RequestMapping("/user/userDetail")
    public String userDetail() {
        return "user/userDetail";
    }






}
