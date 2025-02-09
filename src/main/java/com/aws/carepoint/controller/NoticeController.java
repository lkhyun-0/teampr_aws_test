package com.aws.carepoint.controller;

import com.aws.carepoint.dto.NoticeDto;
import com.aws.carepoint.dto.QnaDto;
import com.aws.carepoint.service.NoticeService;
import com.aws.carepoint.util.SearchCriteria;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

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

    @GetMapping("/noticeContent/{id}")
    public String qnaContent(@PathVariable("id") int articlePk, Model model, RedirectAttributes rttr) {
        NoticeDto notice = noticeService.getNoticeDetail(articlePk);
        model.addAttribute("notice", notice);

        return "notice/noticeContent";
    }


    @GetMapping("/noticeList")
    public String noticeList(@ModelAttribute("scri") SearchCriteria scri, Model model) {
        // 매 요청마다 새로운 데이터를 가져와야 함
        Map<String, Object> data = noticeService.getNoticeList(scri);
        model.addAllAttributes(data); // model에 새로운 데이터 추가

        return "notice/noticeList";
    }


//    @PostMapping("/noticeDeleteAction/{id}")
//    public String noticeDeleteAction(@PathVariable("id") int articlePk, RedirectAttributes rttr) {
//        NoticeDto notice = noticeService.getNoticeDetail(articlePk);
//        noticeService.deleteNotice(notice);
//        rttr.addFlashAttribute("msg", "게시글이 삭제 되었습니다.");
//        return "redirect:/notice/noticeList";
//    }


    @GetMapping("/noticeModify/{id}")
    public String noticeModify(@PathVariable("id") int articlePk, Model model) {
        NoticeDto notice = noticeService.getNoticeDetail(articlePk);
        model.addAttribute("notice", notice);
        return "notice/noticeModify";
    }

    @PostMapping("/noticeModifyAction")
    public String noticeModifyAction(@ModelAttribute NoticeDto notice, RedirectAttributes rttr) {
        noticeService.updateNotice(notice);
        rttr.addFlashAttribute("msg", "게시글이 수정 되었습니다.");
        return "redirect:/notice/noticeContent/" + notice.getArticlePk(); // 상세보기 페이지로 이동
    }
}
