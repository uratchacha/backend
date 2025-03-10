package com.example.lachacha.domain.chats.dto.request;

import lombok.Builder;

@Builder
public record JoinGroupRequestDto(
        Long userId,
        Long chatRoomId
) {
}
