package com.example.lachacha.socket.notifications.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "notification_settings")
public class NotificationSetting {

    @Id
    private Long userId;  // 사용자 ID (PK)

    private boolean pushEnabled = true; // 푸시 알림 ON/OFF
    private boolean webSocketEnabled = true; // WebSocket 알림 ON/OFF

    public NotificationSetting(Long userId) {
        this.userId = userId;
    }
}
