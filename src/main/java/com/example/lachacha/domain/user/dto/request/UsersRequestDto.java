package com.example.lachacha.domain.user.dto.request;

import com.example.lachacha.domain.user.domain.Users;
import lombok.Builder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Builder
public record UsersRequestDto(
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
         String additionalNotificationMethods
) {
    public Users toEntity(BCryptPasswordEncoder bCryptPasswordEncoder) {
        return Users.builder()
                .username(username)
                .password(bCryptPasswordEncoder.encode(password))
                .affiliation(affiliation)
                .career(career)
                .contactInfo(contactInfo)
                .email(email)
                .interestJobCategory(interestJobCategory)
                .interestJobValue(interestJobValue)
                .jobCategory(jobCategory)
                .jobValue(jobValue)
                .interests(interests)
                .participationPurpose(participationPurpose)
                .additionalNotificationMethods(additionalNotificationMethods)
                .build();
    }

}
