package com.aws.carepoint.controller;

import com.aws.carepoint.dto.NoticeDto;
import com.aws.carepoint.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/notice")
@RequiredArgsConstructor
public class NoticeController {

    @Autowired
    private final NoticeService noticeService;

    @GetMapping("/noticeWrite")
    public String showWritePage() {

        return "notice/noticeWrite";
    }

    @PostMapping("/noticeWriteAction")
    public String noticeWriteAction(@ModelAttribute NoticeDto notice, RedirectAttributes rttr) {
        noticeService.createNotice(notice);
        rttr.addFlashAttribute("msg", "게시글이 등록 되었습니다.");
        return "redirect:/notice/noticeContent/" + notice.getArticlePk(); // 상세보기 페이지로 이동
    }

    @GetMapping("/noticeContent")
    public String showContentPage() {

        return "notice/noticeContent";
    }
}
