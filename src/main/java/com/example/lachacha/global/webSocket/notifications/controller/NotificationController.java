package com.example.lachacha.global.webSocket.notifications.controller;

import com.example.lachacha.global.webSocket.notifications.entity.Notification;
import com.example.lachacha.socket.notifications.entity.NotificationSetting;
import com.example.lachacha.global.webSocket.notifications.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    // ✅ 사용자 알림 설정 조회
    @GetMapping("/settings/{userId}")
    public NotificationSetting getNotificationSetting(@PathVariable Long userId) {
        return notificationService.getUserNotificationSetting(userId);
    }

    // ✅ 사용자 알림 설정 변경 (푸시 알림 / WebSocket 알림 ON/OFF)
    @PostMapping("/settings/{userId}")
    public void updateNotificationSetting(@PathVariable Long userId,
                                          @RequestParam boolean pushEnabled,
                                          @RequestParam boolean webSocketEnabled) {
        notificationService.updateNotificationSetting(userId, pushEnabled, webSocketEnabled);
    }

    // ✅ 사용자 알림 내역 조회
    @GetMapping("/{userId}")
    public List<Notification> getUserNotifications(@PathVariable Long userId) {
        return notificationService.getUserNotifications(userId);
    }

    // ✅ 알림 읽음 처리
    @PostMapping("/read/{notificationId}")
    public void markNotificationAsRead(@PathVariable Long notificationId) {
        notificationService.markNotificationAsRead(notificationId);
    }
}
