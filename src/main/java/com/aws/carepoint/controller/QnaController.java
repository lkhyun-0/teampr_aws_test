package com.aws.carepoint.controller;

import com.aws.carepoint.dto.QnaDto;
import com.aws.carepoint.mapper.QnaMapper;
import com.aws.carepoint.util.PageMaker;
import com.aws.carepoint.util.SearchCriteria;
import com.aws.carepoint.service.QnaService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@RequestMapping("/qna")
public class QnaController {

    @Autowired
    private final QnaService qnaService;

    @Value("${file.upload-dir}") // application.yml에서 설정 값 가져오기
    private String uploadDir;

    @GetMapping("/qnaList")
    public String qnaList(@ModelAttribute("scri") SearchCriteria scri, Model model) {
        // ✅ 매 요청마다 새로운 데이터를 가져와야 함
        Map<String, Object> data = qnaService.getQnaList(scri);
        model.addAllAttributes(data); // ✅ model에 새로운 데이터 추가
        System.out.println(data);
        return "qna/qnaList";
    }

    @GetMapping("/qnaContent/{id}")
    public String qnaContent(
            @PathVariable("id") int articlePk,
            Model model,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        String path="";
        int userPk;
        int authLevel;

        QnaDto qnaDto = qnaService.getQnaDetail(articlePk);
        model.addAttribute("qna", qnaDto);

        if(session.getAttribute("userPk") == null) {
            userPk = 0;
            redirectAttributes.addFlashAttribute("msg", "로그인 후 사용 가능합니다.");
            path = "redirect:/qna/qnaList";
        } else {
            userPk = (Integer)session.getAttribute("userPk");
        }

        if(session.getAttribute("authLevel") == null) {
            authLevel = 0;
        } else {
            authLevel = (Integer)session.getAttribute("authLevel");
        }

        // 관리자는 모든 게시글에 접근 가능
        if(authLevel == 7) {
            path = "qna/qnaContent";
        }
        // 회원은 자신이 작성한 글과 해당 글의 origin_num이 같은 모든 게시글 열람 가능
        else if(authLevel == 3) {
            // 회원이 작성한 origin_num 가져오기
            List<Integer> userOriginNums = qnaService.getUserOriginNums(userPk);

            // 현재 게시글의 origin_num이 회원의 origin_num 목록에 포함되어 있는지 확인
            if(userOriginNums.contains(qnaDto.getOriginNum())) {
                path = "qna/qnaContent";
            } else {
                redirectAttributes.addFlashAttribute("msg", "열람 권한이 없습니다.");
                path = "redirect:/qna/qnaList";
            }
        }
        return path;
    }

    @GetMapping("/qnaWrite")
    public String qnaWrite(HttpSession session, RedirectAttributes rttr) {

        // 세션에서 사용자 정보 가져오기
        Integer userPk = (Integer) session.getAttribute("userPk");

        if (userPk == null || userPk < 1) {
            rttr.addFlashAttribute("msg", "로그인 후 사용 가능합니다.");
            return "redirect:/qna/qnaList";
        }
        return "qna/qnaWrite";
    }

    @PostMapping("/qnaWriteAction")
    public String qnaWriteAction(
            @ModelAttribute QnaDto qnaDto,
            @RequestParam("attachfile") MultipartFile attachfile,
            HttpSession session,
            RedirectAttributes rttr) {

        String uploadDir = "D:/team_Pr/workspace/uploads";

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
                qnaDto.setFilename(storedFilename);

            } catch (IOException e) {
                e.printStackTrace();
                rttr.addFlashAttribute("msg", "파일 업로드 중 오류가 발생했습니다.");
                return "redirect:/qna/qnaWrite";
            }
        }

        Integer userPk = (Integer) session.getAttribute("userPk");
        qnaDto.setUserPk(userPk);
        System.out.println("DtoFilename =======================================> " + qnaDto.getFilename());
        int value = qnaService.createQna(qnaDto);

        String path="";
        if (value > 0) {
            rttr.addFlashAttribute("msg", "게시글이 등록 되었습니다.");
            path = "redirect:/qna/qnaContent/" + qnaDto.getArticlePk(); // 상세보기 페이지로 이동
        } else {
            rttr.addFlashAttribute("msg", "게시글 등록 중 오류가 발생했습니다.");
            path = "redirect:/qna/qnaList";
        }
        return path;
    }

    @PostMapping("/qnaDeleteAction/{id}")
    public String qnaDeleteAction(
            @PathVariable("id") int articlePk,
            RedirectAttributes redirectAttributes) {
        QnaDto qnaDto = qnaService.getQnaDetail(articlePk);
        int value = qnaService.deleteQna(qnaDto);

        if (value >= 1) {
            redirectAttributes.addFlashAttribute("msg", "게시글이 삭제 되었습니다.");
        } else {
            redirectAttributes.addFlashAttribute("msg", "게시글을 삭제 할 수 없습니다.");
        }

        return "redirect:/qna/qnaList";
    }

    @GetMapping("/qnaModify/{id}")
    public String qnaModify(@PathVariable("id") int articlePk, Model model) {
        QnaDto qnaDto = qnaService.getQnaDetail(articlePk);
        model.addAttribute("qna", qnaDto);
        return "qna/qnaModify";
    }

    @PostMapping("/qnaModifyAction")
    public String qnaModifyAction(
            @ModelAttribute QnaDto qnaDto,
            RedirectAttributes redirectAttributes,
            HttpSession session,
            @RequestParam("attachfile") MultipartFile attachfile) {

        String uploadDir = "D:/team_Pr/workspace/uploads";

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
                qnaDto.setFilename(storedFilename);

            } catch (IOException e) {
                e.printStackTrace();
                redirectAttributes.addFlashAttribute("msg", "파일 업로드 중 오류가 발생했습니다.");
                return "redirect:/qna/qnaModify";
            }
        }

        int userPk = (int) session.getAttribute("userPk");

        String path="";
        if(qnaDto.getUserPk() == userPk) {
            int value = qnaService.updateQna(qnaDto);

            if(value == 1) {
                redirectAttributes.addFlashAttribute("msg", "게시글이 수정 되었습니다.");
                path = "redirect:/qna/qnaContent/" + qnaDto.getArticlePk(); // 상세보기 페이지로 이동
            }
        } else {
            redirectAttributes.addFlashAttribute("msg", "자신의 게시글만 수정 할 수 있습니다.");
            path = "redirect:/qna/qnaList";
        }
        return path;
    }

    @GetMapping("/qnaReply/{id}")
    public String qnaReply(
            @PathVariable("id") int articlePk,
            Model model,
            RedirectAttributes redirectAttributes,
            HttpSession session) {
        QnaDto qna = qnaService.getQnaDetail(articlePk);
        model.addAttribute("qna", qna);

        int authLevel = (int) session.getAttribute("authLevel");

        String path="";
        if(authLevel == 7) {
            int value = qnaService.hasQnaReply(qna);
            if (value > 1) {
                redirectAttributes.addFlashAttribute("msg", "이미 답변이 존재합니다.");
                return "redirect:/qna/qnaList";
            }
            path = "qna/qnaReply";
        } else {
            redirectAttributes.addFlashAttribute("msg", "관리자만 답변을 작성 할 수 있습니다.");
            path = "redirect:/qna/qnaList";
        }
        return path;
    }

    @PostMapping("/qnaReplyAction")
    public String qnaReplyAction(
            @ModelAttribute QnaDto qnaDto,
            RedirectAttributes redirectAttributes,
            HttpSession session,
            @RequestParam("attachfile") MultipartFile attachfile) {

        String uploadDir = "D:/team_Pr/workspace/uploads";

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
                qnaDto.setFilename(storedFilename);

            } catch (IOException e) {
                e.printStackTrace();
                redirectAttributes.addFlashAttribute("msg", "파일 업로드 중 오류가 발생했습니다.");
                return "redirect:/qna/qnaModify";
            }
        }

        int userPk = (int) session.getAttribute("userPk");
        qnaDto.setUserPk(userPk);
        int value = qnaService.createQnaReply(qnaDto);

        String path="";
        if (value == 1) {
            redirectAttributes.addFlashAttribute("msg", "답변이 등록 되었습니다.");
            path = "redirect:/qna/qnaContent/" + qnaDto.getArticlePk();
        } else {
            redirectAttributes.addFlashAttribute("msg", "입력이 잘못 되었습니다.");
            path = "redirect:/qna/qnaReply/" + qnaDto.getArticlePk();
        }
        return path;
    }
}
