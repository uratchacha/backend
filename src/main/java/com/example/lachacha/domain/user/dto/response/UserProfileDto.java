package com.example.lachacha.domain.user.dto.response;

import com.example.lachacha.domain.user.domain.Users;

public record UserProfileDto(
        Long id,
        String username,
        String password,
        String affiliation,
        String career,
        String contactInfo,
        String email,
        String interestJobCategory,
        String interestJobValue,
        String jobCategory,
        String jobValue,
        String interests,
        String participationPurpose,
        String additionalNotificationMethods,
        String nickName
) {
    public static UserProfileDto of(Users user) {
        return new UserProfileDto(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                user.getAffiliation(),
                user.getCareer(),
                user.getContactInfo(),
                user.getEmail(),
                user.getInterestJobCategory(),
                user.getInterestJobValue(),
                user.getJobCategory(),
                user.getJobValue(),
                user.getInterests(),
                user.getParticipationPurpose(),
                user.getAdditionalNotificationMethods(),
                user.getNickName()
        );
    }
}
