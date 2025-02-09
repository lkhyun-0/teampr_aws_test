package com.aws.carepoint.controller;

import com.aws.carepoint.dto.QnaDto;
import com.aws.carepoint.util.SearchCriteria;
import com.aws.carepoint.service.QnaService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/qna")
public class QnaController {

    @Autowired
    private final QnaService qnaService;

    @GetMapping("/qnaList")
    public String qnaList(@ModelAttribute("scri") SearchCriteria scri, Model model) {
        // ✅ 매 요청마다 새로운 데이터를 가져와야 함
        Map<String, Object> data = qnaService.getQnaList(scri);
        model.addAllAttributes(data); // ✅ model에 새로운 데이터 추가

        return "qna/qnaList";
    }

    @GetMapping("/qnaContent/{id}")
    public String qnaContent(@PathVariable("id") int articlePk, Model model, RedirectAttributes rttr) {
        QnaDto qna = qnaService.getQnaDetail(articlePk);
        model.addAttribute("qna", qna);

        return "qna/qnaContent";
    }

    @GetMapping("/qnaWrite")
    public String qnaWrite() {

        return "qna/qnaWrite";
    }

    @PostMapping("/qnaWriteAction")
    public String qnaWriteAction(@ModelAttribute QnaDto qna, RedirectAttributes rttr) {
        qnaService.createQna(qna);
        rttr.addFlashAttribute("msg", "게시글이 등록 되었습니다.");
        return "redirect:/qna/qnaContent/" + qna.getArticlePk(); // 상세보기 페이지로 이동
    }

    @PostMapping("/qnaDeleteAction/{id}")
    public String qnaDeleteAction(@PathVariable("id") int articlePk, RedirectAttributes rttr) {
        QnaDto qna = qnaService.getQnaDetail(articlePk);
        qnaService.deleteQna(qna);
        rttr.addFlashAttribute("msg", "게시글이 삭제 되었습니다.");
        return "redirect:/qna/qnaList";
    }

    @GetMapping("/qnaModify/{id}")
    public String qnaModify(@PathVariable("id") int articlePk, Model model) {
        QnaDto qna = qnaService.getQnaDetail(articlePk);
        model.addAttribute("qna", qna);
        return "qna/qnaModify";
    }

    @PostMapping("/qnaModifyAction")
    public String qnaModifyAction(@ModelAttribute QnaDto qna, RedirectAttributes rttr) {
        qnaService.updateQna(qna);
        rttr.addFlashAttribute("msg", "게시글이 수정 되었습니다.");
        return "redirect:/qna/qnaContent/" + qna.getArticlePk(); // 상세보기 페이지로 이동
    }

    @GetMapping("/qnaReply/{id}")
    public String qnaReply(@PathVariable("id") int articlePk, Model model, RedirectAttributes rttr) {
        QnaDto qna = qnaService.getQnaDetail(articlePk);
        model.addAttribute("qna", qna);

        int value = qnaService.hasQnaReply(qna);
        System.out.println("value ====================> " + value);
        if (value > 1) {
            rttr.addFlashAttribute("msg", "이미 답변이 존재합니다.");
            return "redirect:/qna/qnaList";
        }
        return "qna/qnaReply";
    }

    @PostMapping("/qnaReplyAction")
    public String qnaReplyAction(@ModelAttribute QnaDto qna, RedirectAttributes rttr) {
        qnaService.createQnaReply(qna);
        rttr.addFlashAttribute("msg", "답변이 등록 되었습니다.");
        return "redirect:/qna/qnaContent/" + qna.getArticlePk(); // 상세보기 페이지로 이동
    }
}
