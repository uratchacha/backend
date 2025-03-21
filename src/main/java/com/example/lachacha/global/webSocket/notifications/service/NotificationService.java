package com.example.lachacha.global.webSocket.notifications.service;

import com.example.lachacha.global.webSocket.notifications.NotificationHandler;
import com.example.lachacha.global.webSocket.notifications.entity.Notification;
import com.example.lachacha.global.webSocket.notifications.repository.NotificationRepository;
import com.example.lachacha.global.webSocket.notifications.repository.NotificationSettingRepository;
import com.example.lachacha.socket.notifications.entity.NotificationSetting;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationSettingRepository settingRepository;
    private final NotificationHandler notificationHandler;

    // ✅ 사용자 알림 설정 조회
    public NotificationSetting getUserNotificationSetting(Long userId) {
        return settingRepository.findById(userId)
                .orElse(new NotificationSetting(userId)); // 기본값 (ON) 생성
    }

    // ✅ 사용자 알림 설정 변경
    public void updateNotificationSetting(Long userId, boolean pushEnabled, boolean webSocketEnabled) {
        NotificationSetting setting = getUserNotificationSetting(userId);
        setting.setPushEnabled(pushEnabled);
        setting.setWebSocketEnabled(webSocketEnabled);
        settingRepository.save(setting);
    }

    // ✅ 알림 내역 저장
    public void saveNotification(Long userId, String type, String message) {
        Notification notification = new Notification(userId, type, message);
        notificationRepository.save(notification);
    }

    // ✅ 사용자의 알림 내역 조회
    public List<Notification> getUserNotifications(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    // ✅ 알림 읽음 처리
    public void markNotificationAsRead(Long notificationId) {
        Optional<Notification> notification = notificationRepository.findById(notificationId);
        notification.ifPresent(n -> {
            n.setRead(true);
            notificationRepository.save(n);
        });
    }

    // ✅ 특정 사용자에게 WebSocket 알림 전송 (새롭게 추가)
    public void sendWebSocketNotification(Long userId, String message) {
        try {
            notificationHandler.sendNotification(userId, message);
        } catch (IOException e) {
            System.err.println("❌ WebSocket 알림 전송 실패: " + e.getMessage());
        }
    }

    public void notifyConferenceEnd(Long userId) {
        saveNotification(userId, "CONFERENCE_END", "금일 네트워킹이 종료되었어요, 내일 다시 진행해주세요.");
    }

}
