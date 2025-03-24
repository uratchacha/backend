package com.example.lachacha.domain.user.dto.request;

import lombok.Builder;

import java.util.List;

@Builder
public record UserUpdateRequestDto(
         String name,
         String email,
         String affiliation,
         String career,
         String contactInfo,
         String interestJobCategory,
         String interestJobValue,
         List<String> interests,
         String jobCategory,
         String jobValue,
         String participationPurpose
) {
}
