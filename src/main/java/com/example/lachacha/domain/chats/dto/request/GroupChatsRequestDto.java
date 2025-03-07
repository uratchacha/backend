package com.example.lachacha.domain.chats.dto.request;

public record GroupChatsRequestDto
        (
                Long userId,
                int maxSize
        )
{
}
