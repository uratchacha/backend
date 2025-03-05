package com.example.lachacha.domain.chats.dto.request;

import lombok.Builder;

@Builder
public record ChatsRequestDto(
        Long requesterId,
        Long receiverId
) {
}
