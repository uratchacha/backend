package com.example.lachacha.domain.chats.dto.response;

import com.example.lachacha.domain.chats.domain.ChatRoom;
import com.example.lachacha.domain.user.domain.Users;

import java.util.List;

public record ChatRoomResponseDto(
         Long id,
         int maxSize,
         List<Users> members
) {
    public static ChatRoomResponseDto from(ChatRoom chatRoom) {
        return new ChatRoomResponseDto(
                chatRoom.getId(),
                chatRoom.getMaxSize(),
                chatRoom.getMembers()
        );
    }
}
