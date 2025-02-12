package com.aws.carepoint.service;


import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.service.DefaultMessageService;
import net.nurigo.sdk.message.exception.NurigoMessageNotReceivedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class SmsService {

    private final DefaultMessageService messageService;
    @Value("${coolsms.sender.phone}")
    private String senderPhone;

    public SmsService(
            @Value("${coolsms.api.key}") String apiKey,
            @Value("${coolsms.api.secret}") String apiSecret
    ) {
        this.messageService = NurigoApp.INSTANCE.initialize(apiKey, apiSecret, "https://api.coolsms.co.kr");
    }

    public void sendSms(String to, String text) {
        if (to == null || to.isEmpty()) {
            System.out.println("⚠️ 수신자의 전화번호가 없습니다.");
            return;
        }

        Message message = new Message();
        message.setFrom(senderPhone);  // 설정에서 불러온 발신번호
        message.setTo(to);
        message.setText(text);

        try {
            messageService.send(message);
            System.out.println("✅ 문자 전송 성공! (수신자: " + to + ")");
        } catch (NurigoMessageNotReceivedException e) {
            System.out.println("❌ 문자 전송 실패: " + e.getMessage());
            System.out.println("실패한 메시지: " + e.getFailedMessageList());
        } catch (Exception e) {
            System.out.println("❌ 예외 발생: " + e.getMessage());
        }
    }
}