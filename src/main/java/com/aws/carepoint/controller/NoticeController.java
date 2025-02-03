package com.aws.carepoint.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/notice")
public class NoticeController {

    @GetMapping("/noticeWrite")
    public String showWritePage() {

        return "notice/noticeWrite";
    }

    @GetMapping("/noticeContent")
    public String showContentPage() {

        return "notice/noticeContent";
    }
}
