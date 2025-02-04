package com.aws.carepoint.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/qna")
public class QnaController {

    @GetMapping("/qnaList")
    public String qnaList() {

        return "qna/qnaList";
    }

    @GetMapping("/qnaContent")
    public String qnaBoardContent() {

        return "qna/qnaContent";
    }

    @GetMapping("/qnaWrite")
    public String qnaBoardWrite() {

        return "qna/qnaWrite";
    }

    @GetMapping("/qnaModify")
    public String qnaBoardModify() {

        return "qna/qnaModify";
    }

    @GetMapping("/qnaReply")
    public String qnaBoardReply() {

        return "qna/qnaReply";
    }
}
