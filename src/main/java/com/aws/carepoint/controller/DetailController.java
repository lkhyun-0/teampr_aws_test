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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
            userInfo = new UsersDto();
        }

        if (userDetail == null) {
            userDetail = new DetailDto();
        }

        model.addAttribute("userInfo", userInfo);
        model.addAttribute("userDetail", userDetail);

        return "user/myPage";
    }
    @PostMapping("updateInfo")
    public ResponseEntity<?> updateInfo(@RequestBody Map<String, Object> requestBody) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            UsersDto usersDto = objectMapper.convertValue(requestBody.get("usersDto"), UsersDto.class);
            DetailDto detailDto = objectMapper.convertValue(requestBody.get("detailDto"), DetailDto.class);

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


    // 프로필사진업로드
    @PostMapping("uploadProfileImage")
    public ResponseEntity<?> uploadProfileImage(@RequestParam("profileImage") MultipartFile file, HttpSession session) {
        Object userPkObj = session.getAttribute("userPk");
        if (userPkObj == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("success", false, "message", "로그인이 필요합니다."));
        }

        int userPk = (Integer) userPkObj;

        try {
            // 저장할 디렉토리 경로 설정
            String uploadDir = System.getProperty("user.dir") + "/uploads/user/" + userPk;
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();

            // UUID를 이용해 파일명 중복 방지
            String fileExtension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
            String uniqueFileName = UUID.randomUUID().toString() + fileExtension;
            String filePath = uploadDir + "/" + uniqueFileName;
            Path path = Path.of(filePath);

            // 파일 저장
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

            // DB에 저장할 경로 (웹에서 접근 가능하도록 상대 경로 저장)
            String dbFilePath = "/uploads/user/" + userPk + "/" + uniqueFileName;

            // DB 업데이트 실행
            detailService.updateProfileImage(userPk, dbFilePath);
            System.out.println("DB 저장 완료: " + dbFilePath); // 로그 추가

            return ResponseEntity.ok(Map.of("success", true, "imagePath", dbFilePath));

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("success", false, "message", "파일 업로드 실패: " + e.getMessage()));
        }
    }

}













