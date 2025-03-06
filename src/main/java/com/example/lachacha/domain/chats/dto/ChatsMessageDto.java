package com.example.lachacha.domain.chats.dto;

public record ChatsMessageDto(
        Long chatRoomId,
        String message,
        String senderName
) {
}
