package com.example.lachacha.domain.chats.dto.request;

import lombok.Builder;

@Builder
public record PrivateChatsRequestDto(
        Long requesterId,
        Long receiverId
) {
}
