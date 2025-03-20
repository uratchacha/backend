package com.example.lachacha.domain.user.dto.response;

import com.example.lachacha.domain.user.domain.Users;

public record MyPageResponseDto(
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
    public static MyPageResponseDto of(Users user) {
        return new MyPageResponseDto(
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
