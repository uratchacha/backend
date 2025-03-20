package com.example.lachacha.domain.user.dto.request;

import com.example.lachacha.domain.user.domain.Users;
import lombok.Builder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Builder
public record UsersRequestDto(
         String username,
         String password,
         String introduction,
         String roles,
         String interests,
         String participationPurpose,
         String additionalNotificationMethods
) {
    public Users toEntity() {
        return Users.builder()
                .username(username)
                .password(password)
                .introduction(introduction)
                .roles(roles)
                .interests(interests)
                .participationPurpose(participationPurpose)
                .additionalNotificationMethods(additionalNotificationMethods)
                .build();
    }

}
