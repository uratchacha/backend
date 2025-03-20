package com.example.lachacha.domain.user.dto.request;

import com.example.lachacha.domain.user.domain.Users;
import lombok.Builder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;

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
         List<String> interests,
         String participationPurpose
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
                .build();
    }

}
