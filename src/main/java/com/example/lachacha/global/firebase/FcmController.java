package com.example.lachacha.global.firebase;


import com.example.lachacha.global.firebase.dto.FcmNotificationRequest;
import com.example.lachacha.global.firebase.dto.FcmTokenRequest;
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

    //map방식
    /*@PostMapping("/register-token")
    public String registerFcmToken(@RequestBody Map<String, String> body) {
        try {
            Long userId = Long.parseLong(body.get("userId"));
            String token = body.get("token");
            fcmService.saveFcmToken(userId, token);
            return "✅ FCM 토큰 등록 완료!";
        } catch (Exception e) {
            return "❌ FCM 토큰 등록 실패: " + e.getMessage();
        }
    }
    */
    // ✅ 프론트에서 FCM 토큰 등록
    @PostMapping("/register-token")
    public String registerFcmToken(@RequestBody FcmTokenRequest request) {
        try {
            fcmService.saveFcmToken(request.getUserId(), request.getToken());
            return "✅ FCM 토큰 등록 완료!";
        } catch (Exception e) {
            return "❌ FCM 토큰 등록 실패: " + e.getMessage();
        }
    }

    // ✅ 푸시 알림 전송 테스트용 API
    @PostMapping("/send-notification")
    public void sendNotification(@RequestBody FcmNotificationRequest request) {
        fcmService.sendPushNotificationByUserId(
                request.getUserId(),
                request.getTitle(),
                request.getBody()
        );
    }
}