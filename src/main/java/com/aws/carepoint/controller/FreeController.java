package com.aws.carepoint.controller;

import com.aws.carepoint.dto.CommentDto;
import com.aws.carepoint.dto.FreeDto;
import com.aws.carepoint.service.CommentService;
import com.aws.carepoint.service.FreeService;
import com.aws.carepoint.service.RecommendService;
import com.aws.carepoint.util.SearchCriteria;
import jakarta.servlet.http.HttpSession;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/free")
public class FreeController {

    @Autowired
    private FreeService freeService;

    @Autowired
    private RecommendService recommendService;

    @Autowired
    private CommentService commentService;

    @Value("${file.upload-dir}") // application.yml에서 설정 값 가져오기
    private String uploadDir;

    @GetMapping("/freeList")
    public String freeList(
            @ModelAttribute("scri") SearchCriteria scri,
            Model model) {

        Map<String, Object> result = freeService.getFreeList(scri);
        model.addAllAttributes(result);

        return "free/freeList";
    }

    @GetMapping("/freeContent/{id}")
    public String freeContent(
            @PathVariable("id") int articlePk,
            Model model) {

        int value = freeService.addviewcnt(articlePk);

        if (value > 0) {
            //추천
            int recomcount = recommendService.getRecommendCount(articlePk);

            // 댓글
            List<CommentDto> clist = commentService.getComment(articlePk);
            int cmtcount = clist.size();

            // 게시글
            FreeDto free = freeService.getFreeDetail(articlePk);

            model.addAttribute("free", free);
            model.addAttribute("clist", clist);
            model.addAttribute("cmtcount", cmtcount);
            model.addAttribute("recomcount", recomcount);
        } else {
            return "free/freeList";
        }

        return "free/freeContent";
    }

    @GetMapping("/freeWrite")
    public String freeWrite(
            HttpSession session,
            RedirectAttributes rttr
            ) {
        // 세션에서 사용자 정보 가져오기
        Integer userPk = (Integer) session.getAttribute("userPk");

        if (userPk == null || userPk < 1) {
            rttr.addFlashAttribute("msg", "로그인해야 가능합니다.");
            return "redirect:/free/freeList";
        }

        return "free/freeWrite";
    }

    @PostMapping("/freeWriteAction")
    public String freeWriteAction(
            @ModelAttribute FreeDto freeDto,
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
                freeDto.setFilename(storedFilename);

            } catch (IOException e) {
                e.printStackTrace();
                rttr.addFlashAttribute("msg", "파일 업로드 중 오류 발생");
                return "redirect:/free/freeWrite";
            }
        }

        Integer userPk = (Integer) session.getAttribute("userPk");
        freeDto.setUserPk(userPk);

        int value = freeService.writeFree(freeDto);

        if (value > 0) {
            rttr.addFlashAttribute("msg", "게시글이 작성되었습니다.");
        } else {
            rttr.addFlashAttribute("msg", "게시글 작성 중 오류 발생");
        }

        return "redirect:/free/freeList";
    }

    @GetMapping("/freeModify/{id}")
    public String freeModify(
            @PathVariable("id") int articlePk,
            Model model
    ) {

        FreeDto free = freeService.getFreeDetail(articlePk);
        model.addAttribute("free", free);

        return "free/freeModify";
    }

    @PostMapping("/freeModifyAction")
    public String freeModify(@ModelAttribute FreeDto freeDto,
                             @RequestParam("attachfile") MultipartFile attachfile,
                             RedirectAttributes rttr
    ) {
        String uploadDir = "C:/KIS_project/uploads";

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
                freeDto.setFilename(storedFilename);

            } catch (IOException e) {
                e.printStackTrace();
                rttr.addFlashAttribute("msg", "파일 업로드 중 오류 발생");
                return "redirect:/free/freeWrite";
            }
        }

        System.out.println(freeDto);

        int value = freeService.modifyFree(freeDto);

        if (value > 0) {
            rttr.addFlashAttribute("msg", "게시글이 작성되었습니다.");
        } else {
            rttr.addFlashAttribute("msg", "게시글 작성 중 오류 발생");
        }

        return "redirect:/free/freeList";
    }

    @PostMapping("/freeDelete/{articlePk}")
    @ResponseBody
    public ResponseEntity<Void> freeDelete(@PathVariable("articlePk") int articlePk) {
        int result = freeService.deleteArticle(articlePk); // 삭제 성공 여부 확인

        if (result > 0) {
            return ResponseEntity.ok().build(); // 200 OK (삭제 성공)
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // 500 ERROR (삭제 실패)
        }
    }
}
