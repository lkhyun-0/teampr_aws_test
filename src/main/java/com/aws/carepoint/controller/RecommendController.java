package com.aws.carepoint.controller;

import com.aws.carepoint.dto.RecommendDto;
import com.aws.carepoint.dto.UsersDto;
import com.aws.carepoint.service.RecommendService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/recommend")
public class RecommendController {

    @Autowired
    private RecommendService recommendService;

    @PostMapping("/toggle")
    public ResponseEntity<?> toggleRecommend(
            @RequestParam("articlePk") int articlePk,
            HttpSession session) {

        // 세션에서 사용자 정보 가져오기
        Integer userPk = (Integer) session.getAttribute("userPk");

        if (userPk == null || userPk < 1) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("로그인이 필요합니다.");
        }

        RecommendDto result = recommendService.toggleRecommend(userPk, articlePk);
        return ResponseEntity.ok(result);
    }

}
