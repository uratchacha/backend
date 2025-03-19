package com.example.lachacha.domain.user.dto.request;

import lombok.Builder;

@Builder
public record UsersLoginRequest(
        String username,
        String password
) {
}
