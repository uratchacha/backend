package com.example.lachacha.global.webSocket.notifications.repository;

import com.example.lachacha.socket.notifications.entity.NotificationSetting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationSettingRepository extends JpaRepository<NotificationSetting, Long> {
}
