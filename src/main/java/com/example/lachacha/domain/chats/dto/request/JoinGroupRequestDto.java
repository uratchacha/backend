package com.example.lachacha.domain.chats.dto.request;

public record JoinGroupRequestDto(
        Long userId,
        Long chatRoomId
) {
}
