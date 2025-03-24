package com.example.lachacha.global.firebase;

import com.example.lachacha.domain.user.domain.Users;
import com.example.lachacha.global.auth.application.AuthService;
import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class FcmService {

    // ✅ 사용자 FCM 토큰 저장소 (실제 환경에서는 DB를 사용할 것)
    private final Map<Long, String> userFcmTokens = new ConcurrentHashMap<>();
    private final AuthService authService;
    // ✅ 사용자 FCM 토큰 저장 (프론트에서 PWA로 받은 FCM 토큰을 저장)
    @Transactional
    public void saveFcmToken(String token) {
        Users users = authService.findUsersByAuth();
        userFcmTokens.put(users.getId(), token);
        log.info("✅ 사용자 " + users.getId() + "의 FCM 토큰이 저장됨: " + token);
    }

    // ✅ `userId`를 받아서 FCM 토큰을 조회하는 기능
    public String getTokenByUserId(Long userId) {
        return userFcmTokens.get(userId); // ✅ DB를 사용하는 경우, 여기서 DB 조회로 변경 가능
    }

    // ✅ `userId`를 기반으로 푸시 알림 전송 (토큰 자동 조회)
    public void sendPushNotificationByUserId(Long userId,String title, String body) {
        String token = getTokenByUserId(userId);
        if (token == null) {
            log.info("❌ 사용자 " + userId + "의 FCM 토큰을 찾을 수 없음.");
            return;
        }

        sendPushNotification(token, title, body);
    }
    public void sendPushNotification(String token, String title, String body) {
        // ✅ 테스트용 FCM 토큰을 예외 처리에서 제외
        if (token == null || token.isEmpty()) {
            log.info("❌ 유효하지 않은 FCM 토큰: " + token);
            return; // ✅ 유효하지 않은 토큰일 경우 푸시 알림을 보내지 않음
        }

        Message message = Message.builder()
                .setToken(token)
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .build();

        try {
            String response = FirebaseMessaging.getInstance().send(message);
            log.info("✅ FCM 메시지 전송 성공: " + response);
        } catch (Exception e) {
            log.info("❌ FCM 메시지 전송 실패: " + e.getMessage());
        }
    }

    // ✅ `notificationType`을 받아서 메시지를 자동으로 생성하여 전송
    //public void sendPushNotificationByType(Long userId, String notificationType) {
    //    String token = getTokenByUserId(userId);
   //     if (token == null) {
   //         System.err.println("❌ 사용자 " + userId + "의 FCM 토큰을 찾을 수 없음.");
     //       return;
   //     }

   //     String title = "알림";
   //     String body = getMessageForType(notificationType);

   //     sendPushNotification(token, title, body);
  //  }

    // ✅ 메시지 타입별 내용 자동 설정
   // private String getMessageForType(String notificationType) {
   //     return switch (notificationType) {
   //         case "MATCH_REQUEST" -> "매칭 신청: 3분 내로 수락 여부를 알려주지 않으면 자동 취소됩니다.";
    //        case "MATCH_ACCEPTED" -> "매칭 완료: 매칭 요청이 수락되었습니다.";
    //        case "GROUP_MATCH" -> "그룹 매칭 완료: 정원이 모집되어 채팅방이 개설되었습니다.";
    //        case "TABLE_ASSIGNED" -> "테이블 배정 완료: 테이블이 배정되었습니다.";
     //       case "NETWORKING_END" -> "네트워킹 종료: 네트워킹이 종료되었습니다.";
    //        default -> "새로운 알림이 도착했습니다.";
     //   };
   // }

    // ✅ 컨퍼런스 종료 시 모든 사용자에게 알림 전송
   // public void sendConferenceEndNotification(Long userId) {
    //    sendPushNotificationByType(userId, "NETWORKING_END");
   // }
}

