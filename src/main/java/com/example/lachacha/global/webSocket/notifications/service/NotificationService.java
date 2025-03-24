package com.example.lachacha.global.webSocket.notifications.service;

import com.example.lachacha.domain.user.domain.Users;
import com.example.lachacha.global.auth.application.AuthService;
import com.example.lachacha.global.webSocket.notifications.NotificationHandler;
import com.example.lachacha.global.webSocket.notifications.entity.Notification;
import com.example.lachacha.global.webSocket.notifications.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationHandler notificationHandler;
    private final AuthService authService;

    // ✅ 알림 내역 저장
    @Transactional
    public void saveNotification(Long userId, String type, String message) {
        Notification notification = new Notification(userId, type, message);
        notificationRepository.save(notification);
    }

    // ✅ 사용자의 알림 내역 조회
    public List<Notification> getUserNotifications() {
        Users users =authService.findUsersByAuth();
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(users.getId());
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
