package com.example.lachacha.domain.chats.dto.request;

import lombok.Builder;

import java.util.List;

@Builder
public record GroupChatsRequestDto
        (
                List<String> job,
                String career,
                String interests,
                String participationPurpose
        )
{
}
