package com.aws.carepoint.controller;

import com.aws.carepoint.dto.DetailDto;
import com.aws.carepoint.dto.UsersDto;
import com.aws.carepoint.mapper.DetailMapper;
import com.aws.carepoint.mapper.UserMapper;
import com.aws.carepoint.service.DetailService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@RestController // ✅ JSON 데이터를 반환하도록 @RestController 사용
@RequestMapping("/detail/") // ✅ 기본 경로 설정
public class DetailController {
    private final DetailService detailService;
    private final DetailMapper detailMapper;
    private final UserMapper userMapper;


    public DetailController(DetailService detailService, DetailMapper detailMapper, UserMapper userMapper) {
        this.detailService = detailService;
        this.detailMapper = detailMapper;
        this.userMapper = userMapper;

    }
    @GetMapping("{userPk}/info")        // 기본 회원정보 마이페이지에 보이는 것 수정 xx
    public String getUserInfo(@PathVariable int userPk, Model model) {
        Map<String, Object> userFullInfo = detailService.getUserFullInfo(userPk);

        UsersDto userInfo = (UsersDto) userFullInfo.get("userInfo");
        DetailDto userDetail = (DetailDto) userFullInfo.get("userDetail");

        if (userInfo == null) {
            System.out.println("❌ userInfo is NULL, setting default value...");
            userInfo = new UsersDto();
        }

        if (userDetail == null) {
            System.out.println("❌ userDetail is NULL, setting default value...");
            userDetail = new DetailDto();
        }

        model.addAttribute("userInfo", userInfo);
        model.addAttribute("userDetail", userDetail);

        return "user/myPage";
    }









}
