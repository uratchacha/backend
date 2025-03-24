package com.example.lachacha.domain.user.dto.response;

import com.example.lachacha.domain.user.domain.Users;

import java.util.List;

public record MyPageResponseDto(
        String affiliation,
        String career,
        String contactInfo,
        String email,
        String interestJobCategory,
        String interestJobValue,
        String jobCategory,
        String jobValue,
        List<String> interests,
        String participationPurpose,
        String nickName,
        String name

) {
    public static MyPageResponseDto of(Users user) {
        return new MyPageResponseDto(
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
                user.getNickName(),
                user.getName()
        );
    }
}
