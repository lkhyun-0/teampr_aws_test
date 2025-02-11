package com.aws.carepoint.controller;

import com.aws.carepoint.service.SmsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SmsController {

    private final SmsService smsService;

    public SmsController(SmsService smsService) {
        this.smsService = smsService;
    }

    @GetMapping("/send-sms")
    public String sendSms(@RequestParam("to") String to, @RequestParam("text") String text) {       // 누구에게 어떤 메시지를??
        smsService.sendSms(to, text);
        return "문자 전송 요청 완료!";
    }
}