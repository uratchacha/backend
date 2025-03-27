package com.example.lachacha.global.firebase;


import com.example.lachacha.domain.user.domain.Users;
import com.example.lachacha.global.auth.application.AuthService;
import com.example.lachacha.global.firebase.dto.FcmNotificationRequest;
import com.example.lachacha.global.firebase.dto.FcmTokenRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/FCM")
@RequiredArgsConstructor
public class FcmController {

    private final FcmService fcmService;
    private final AuthService authService;


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
    @PostMapping("/register-token")
    public String registerFcmToken(@RequestBody FcmTokenRequest request) {
        try {
            if (request.getUserId() != null) {
                // ✅ userId가 직접 들어온 경우: 테스트용
                fcmService.saveFcmToken(request.getUserId(), request.getToken());
            } else {
                // ✅ 로그인된 사용자 기준: 실제 서비스용
                fcmService.saveFcmToken(request.getToken());
            }
            return "✅ FCM 토큰 등록 완료!";
        } catch (Exception e) {
            return "❌ FCM 토큰 등록 실패: " + e.getMessage();
        }
    }


    // ✅ 푸시 알림 전송 테스트용 API
    @PostMapping("/send-notification")
    public void sendNotificationToCurrentUser(@RequestBody FcmNotificationRequest request) {
    Users user = authService.findUsersByAuth(); 
    fcmService.sendPushNotificationByUserId(
            user.getId(),
            request.getTitle(),
            request.getBody()
    );
}
    }
