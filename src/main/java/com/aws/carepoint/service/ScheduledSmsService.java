package com.aws.carepoint.service;


import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class ScheduledSmsService {
    private final SmsService smsService;

    public ScheduledSmsService(SmsService smsService) {
        this.smsService = smsService;
    }

    // 매일 오전 9시에 실행 (크론 표현식 사용)
    @Scheduled(cron = "0 30 19 * * ?")
    public void sendDailyReminder() {
        String phoneNumber = "01099171102";  // 수신자 번호 이제 해당 회원의 전화번호를 받아와야함
        String message = "오늘 식단과 운동기록을 하셨나요?🥕";

        smsService.sendSms(phoneNumber, message);
    }
}
