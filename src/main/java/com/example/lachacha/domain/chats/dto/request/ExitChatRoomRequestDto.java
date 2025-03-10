package com.example.lachacha.domain.chats.dto.request;


import lombok.Builder;

@Builder
public record ExitChatRoomRequestDto(
        Long userId,
        Long chatRoomId
) {
}
