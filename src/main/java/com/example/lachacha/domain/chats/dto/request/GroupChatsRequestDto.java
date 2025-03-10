package com.example.lachacha.domain.chats.dto.request;

import lombok.Builder;

@Builder
public record GroupChatsRequestDto
        (
                Long userId,
                int maxSize
        )
{
}
