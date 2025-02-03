package com.aws.carepoint.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/qnaBoard")
public class QnaBoardController {

    @GetMapping("/qnaBoardList")
    public String qnaBoardList() {

        return "qnaBoard/qnaBoardList";
    }

    @GetMapping("/qnaBoardContent")
    public String qnaBoardContent() {

        return "qnaBoard/qnaBoardContent";
    }

    @GetMapping("/qnaBoardWrite")
    public String qnaBoardWrite() {

        return "qnaBoard/qnaBoardWrite";
    }

    @GetMapping("/qnaBoardModify")
    public String qnaBoardModify() {

        return "qnaBoard/qnaBoardModify";
    }

    @GetMapping("/qnaBoardReply")
    public String qnaBoardReply() {

        return "qnaBoard/qnaBoardReply";
    }
}
