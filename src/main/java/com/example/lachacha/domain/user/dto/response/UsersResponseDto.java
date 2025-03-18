package com.example.lachacha.domain.user.dto.response;

import com.example.lachacha.domain.user.domain.Users;
import jakarta.persistence.Column;

public record UsersResponseDto(
        String username,
        String password,
        boolean isParticipate,
        boolean notificationsEnabled,
        String introduction,
        String roles,
        String interests,
        String participationPurpose,
        String additionalNotificationMethods
) {
    public static UsersResponseDto of(Users user) {
        return new UsersResponseDto(
                user.getUsername(),
                user.getPassword(),
                user.isParticipate(),
                user.isNotificationsEnabled(),
                user.getIntroduction(),
                user.getRoles(),
                user.getInterests(),
                user.getParticipationPurpose(),
                user.getAdditionalNotificationMethods()
        );
    }
}
