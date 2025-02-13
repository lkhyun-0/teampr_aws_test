package com.aws.carepoint.controller;

import com.aws.carepoint.dto.NoticeDto;
import com.aws.carepoint.service.NoticeService;
import com.aws.carepoint.util.SearchCriteria;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/notice")
@RequiredArgsConstructor
public class NoticeController {

    @Autowired
    private final NoticeService noticeService;

    @Value("${file.upload-dir}") // application.yml에서 설정 값 가져오기
    private String uploadDir;


    // 글 작성
    @GetMapping("/noticeWrite")
    public String noticeWrite(
            HttpSession session,
            RedirectAttributes rttr
    ) {
        // 세션에서 사용자 정보 가져오기
        Integer userPk = (Integer) session.getAttribute("userPk");

        if (userPk == null || userPk < 1) {
            rttr.addFlashAttribute("msg", "로그인해야 가능합니다.");
            return "redirect:/notice/noticeList";
        }

        return "notice/noticeWrite";
    }

    // 글 작성 넘기기
    @PostMapping("/noticeWriteAction")
    public String noticeWriteAction(
            @ModelAttribute NoticeDto noticeDto,
            @RequestParam("attachfile") MultipartFile attachfile,
            HttpSession session,
            RedirectAttributes rttr
    ) {
        String uploadDir = System.getProperty("user.dir") + File.separator + "uploads";

        if (!attachfile.isEmpty()) {
            try {
                // 저장될 파일 이름 (UUID + 원래 파일 확장자)
                String originalFilename = attachfile.getOriginalFilename();
                String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
                String storedFilename = UUID.randomUUID() + fileExtension;

                // 저장할 경로
                String filePath = Paths.get(uploadDir, storedFilename).toString();

                // 디렉토리가 없으면 생성
                File dir = new File(uploadDir);
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                // 파일 저장
                attachfile.transferTo(new File(filePath));

                // DTO에 저장 경로 설정
                noticeDto.setFilename(storedFilename);

            } catch (IOException e) {
                e.printStackTrace();
                rttr.addFlashAttribute("msg", "파일 업로드 중 오류 발생");
                return "redirect:/notice/noticeWrite";
            }
        }

        Integer userPk = (Integer) session.getAttribute("userPk");
        noticeDto.setUserPk(userPk);

        int value = noticeService.createNotice(noticeDto);

        if (value > 0) {
            rttr.addFlashAttribute("msg", "게시글이 작성되었습니다.");
        } else {
            rttr.addFlashAttribute("msg", "게시글 작성 중 오류 발생");
        }
        return "redirect:/notice/noticeList";
    }

    // 공지사항 내용
    @GetMapping("/noticeContent/{id}")
    public String qnaContent(@PathVariable("id") int articlePk, Model model) {
        NoticeDto notice = noticeService.getNoticeDetail(articlePk);
        model.addAttribute("notice", notice);

        return "notice/noticeContent";
    }

    // 공지사항 목록
    @GetMapping("/noticeList")
    public String noticeList(@ModelAttribute("scri") SearchCriteria scri, Model model, HttpSession session) {
        // 매 요청마다 새로운 데이터를 가져와야 함
        Map<String, Object> data = noticeService.getNoticeList(scri);
        model.addAllAttributes(data); // model에 새로운 데이터 추가

        // 현재 로그인한 사용자의 auth_level 가져오기
        Integer userPk = (Integer) session.getAttribute("userPk"); // 로그인한 사용자 ID
        Integer authLevel = 3; // 기본적으로 일반 사용자(3)로 설정

        if (userPk != null) {
            authLevel = noticeService.getUserAuthLevel(userPk); // DB에서 권한 조회
        }

        model.addAttribute("authLevel", authLevel); // Thymeleaf로 전달

        return "notice/noticeList";
    }

    // 글 삭제
    @PostMapping("/deleteNotice/{articlePk}")
    @ResponseBody
    public ResponseEntity<Void> deleteNotice(@PathVariable("articlePk") int articlePk) {
        int result = noticeService.deleteNotice(articlePk); // 삭제 성공 여부 확인

        if (result > 0) {
            return ResponseEntity.ok().build(); // 200 OK (삭제 성공)
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // 500 ERROR (삭제 실패)
        }
    }

    // 글 수정
    @GetMapping("/noticeModify/{id}")
    public String noticeModify(@PathVariable("id") int articlePk, Model model) {
        NoticeDto notice = noticeService.getNoticeDetail(articlePk);
        model.addAttribute("notice", notice);
        return "notice/noticeModify";
    }

    // 글 수정 넘기기
    @PostMapping("/noticeModifyAction")
    public String noticeModifyAction(@ModelAttribute NoticeDto noticeDto,
                             @RequestParam("attachfile") MultipartFile attachfile,
                             RedirectAttributes rttr
    ) {

        String uploadDir = System.getProperty("user.dir") + File.separator + "uploads";

        if (!attachfile.isEmpty()) {
            try {
                // 저장될 파일 이름 (UUID + 원래 파일 확장자)
                String originalFilename = attachfile.getOriginalFilename();
                String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
                String storedFilename = UUID.randomUUID() + fileExtension;

                // 저장할 경로
                String filePath = Paths.get(uploadDir, storedFilename).toString();

                // 디렉토리가 없으면 생성
                File dir = new File(uploadDir);
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                // 파일 저장
                attachfile.transferTo(new File(filePath));

                // DTO에 저장 경로 설정
                noticeDto.setFilename(storedFilename);

            } catch (IOException e) {
                e.printStackTrace();
                rttr.addFlashAttribute("msg", "파일 업로드 중 오류 발생");
                return "redirect:/notice/noticeWrite";
            }
        }

        int value = noticeService.updateNotice(noticeDto);

        if (value > 0) {
            rttr.addFlashAttribute("msg", "게시글이 작성되었습니다.");
        } else {
            rttr.addFlashAttribute("msg", "게시글 작성 중 오류 발생");
        }
        return "redirect:/notice/noticeList";
    }
}