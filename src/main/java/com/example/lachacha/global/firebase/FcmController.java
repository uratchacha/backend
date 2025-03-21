package com.example.lachacha.global.firebase;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class FcmController {

    private final FcmService fcmService;

    @PostMapping("/send")
    public String sendNotification(@RequestParam String token,
                                   @RequestParam String title,
                                   @RequestParam String body) {
        try {
            fcmService.sendPushNotification(token, title, body);
            return "✅ 푸시 알림 전송 성공!";
        } catch (Exception e) {
            return "❌ 푸시 알림 전송 실패: " + e.getMessage();
        }
    }

    @PostMapping("/send-by-type")
    public String sendNotificationByType(@RequestParam Long userId,
                                         @RequestParam String notificationType) {
        try {
            fcmService.sendPushNotificationByType(userId, notificationType);
            return "✅ 푸시 알림 전송 성공 (타입 기반)!";
        } catch (Exception e) {
            return "❌ 푸시 알림 전송 실패 (타입 기반): " + e.getMessage();
        }
    }
}