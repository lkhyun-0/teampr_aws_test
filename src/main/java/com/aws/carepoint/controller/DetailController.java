package com.aws.carepoint.controller;

import com.aws.carepoint.dto.DetailDto;
import com.aws.carepoint.dto.UsersDto;
import com.aws.carepoint.mapper.DetailMapper;
import com.aws.carepoint.mapper.UserMapper;
import com.aws.carepoint.service.DetailService;
import com.aws.carepoint.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final UserService  userService;

    public DetailController(DetailService detailService, UserService userService) {
        this.detailService = detailService;
        this.userService = userService;


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
    @PostMapping("updateInfo")
    public ResponseEntity<?> updateInfo(@RequestBody Map<String, Object> requestBody) {
        try {
            System.out.println("📌 받은 데이터: " + requestBody);  // JSON 데이터 확인
            // JSON에서 usersDto 데이터 추출
            ObjectMapper objectMapper = new ObjectMapper();
            UsersDto usersDto = objectMapper.convertValue(requestBody.get("usersDto"), UsersDto.class);
            DetailDto detailDto = objectMapper.convertValue(requestBody.get("detailDto"), DetailDto.class);

            System.out.println("📌 변환된 UsersDto: " + usersDto);
            System.out.println("📌 변환된 DetailDto: " + detailDto);


            // 두 개의 DTO 업데이트 실행
            boolean isUserUpdated = detailService.updateUserInfo(usersDto);
            boolean isDetailUpdated = detailService.updateDetailInfo(detailDto);

            if (isUserUpdated || isDetailUpdated) {
                return ResponseEntity.ok().body(Map.of("success", true, "message", "회원 정보 수정 완료"));
            } else {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "회원 정보 수정 실패"));
            }
        } catch (Exception e) {
            e.printStackTrace();  // ✅ 오류 메시지를 콘솔에 출력
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "서버 오류 발생", "error", e.getMessage()));
        }
    }












}
