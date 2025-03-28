package com.example.lachacha.domain.chats.dto.response;

import com.example.lachacha.domain.chats.domain.GroupChatRoom;
import com.example.lachacha.domain.user.domain.Users;

import java.util.List;

public record GroupChatRoomResponseDto(
        Long id,
        int maxSize,
        int members,
        List<String> job,
        String career,
        String interests,
        String participationPurpose
) {
    public static GroupChatRoomResponseDto from(GroupChatRoom groupChatRoom) {
        return new GroupChatRoomResponseDto(
                groupChatRoom.getId(),
                groupChatRoom.getMaxSize(),
                groupChatRoom.getMembers().size(),
                groupChatRoom.getJob(),
                groupChatRoom.getCareer(),
                groupChatRoom.getInterests(),
                groupChatRoom.getParticipationPurpose()
        );
    }
}
