package com.aws.carepoint.controller;

import com.aws.carepoint.dto.QnaDto;
import com.aws.carepoint.util.SearchCriteria;
import com.aws.carepoint.service.QnaService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
    public String qnaContent(@PathVariable("id") int articlePk, Model model) {
        QnaDto qna = qnaService.getQnaDetail(articlePk);
        model.addAttribute("qna", qna);
        return "qna/qnaContent";
    }

    @GetMapping("/qnaWrite")
    public String qnaWrite() {

        return "qna/qnaWrite";
    }

    @GetMapping("/qnaModify/{id}")
    public String qnaModify(@PathVariable("id") int articlePk, Model model) {
        QnaDto qna = qnaService.getQnaDetail(articlePk);
        model.addAttribute("qna", qna);
        return "qna/qnaModify";
    }

    @PostMapping("/qnaModifyAction")
    public String qnaModifyAction(@ModelAttribute QnaDto qna) {
        qnaService.updateQna(qna);
        return "redirect:/qna/qnaContent/" + qna.getArticlePk(); // 상세보기 페이지로 이동
    }

    @GetMapping("/qnaReply")
    public String qnaBoardReply() {

        return "qna/qnaReply";
    }
}
