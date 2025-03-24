package com.example.lachacha.global.webSocket.notifications.controller;

import com.example.lachacha.global.webSocket.notifications.entity.Notification;
import com.example.lachacha.global.webSocket.notifications.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    // ✅ 사용자 알림 내역 조회
    @GetMapping()
    public List<Notification> getUserNotifications() {
        return notificationService.getUserNotifications();
    }

    // ✅ 알림 읽음 처리
    @PostMapping("/read/{notificationId}")
    public void markNotificationAsRead(@PathVariable Long notificationId) {
        notificationService.markNotificationAsRead(notificationId);
    }
}
