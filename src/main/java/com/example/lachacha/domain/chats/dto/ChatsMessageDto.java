package com.example.lachacha.domain.chats.dto;

import java.time.LocalDateTime;

public record ChatsMessageDto(
        LocalDateTime createTime,
        Long chatRoomId,
        String message,
        String senderName
) {
}
