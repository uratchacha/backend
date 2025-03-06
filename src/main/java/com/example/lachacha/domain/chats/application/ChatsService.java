package com.example.lachacha.domain.chats.application;

import com.example.lachacha.domain.chats.domain.ChatRoomRepository;
import com.example.lachacha.domain.chats.domain.PrivateChatRoom;
import com.example.lachacha.domain.chats.dto.request.PrivateChatsRequestDto;
import com.example.lachacha.domain.chats.exception.ChatsException;
import com.example.lachacha.domain.user.application.UsersService;
import com.example.lachacha.domain.user.domain.Users;
import com.example.lachacha.global.exception.MyErrorCode;
import com.example.lachacha.global.webSocket.notifications.NotificationHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ChatsService
{
    private final NotificationHandler notificationHandler;

    private final ChatRoomRepository chatRoomRepository;

    private final UsersService userService;
    public void requestChatRoom(Long requesterId, Long receiverId)
    {
        try {
            String notificationMessage = createNotificationJson(requesterId,receiverId);
            notificationHandler.sendNotification(receiverId, notificationMessage);
        } catch (IOException e) {
            throw new ChatsException(MyErrorCode.NOTIFICATION_ERROR);
        }
    }

    private String createNotificationJson(Long requesterId, Long receiverId) throws JsonProcessingException
    {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> notificationData = new HashMap<>();
        notificationData.put("requesterId", requesterId);
        notificationData.put("receiverId", receiverId);
        notificationData.put("message", "새로운 채팅 요청이 왔습니다.");
        return mapper.writeValueAsString(notificationData);
    }

    public void acceptChatRoom(PrivateChatsRequestDto chatsRequestDto)
    {
        Users users1 = userService.findUsersById(chatsRequestDto.userId1());
        Users users2 = userService.findUsersById(chatsRequestDto.userId2());
        PrivateChatRoom privateChatRoom= PrivateChatRoom.builder()
                .user1(users1)
                .user2(users2)
                .build();
        chatRoomRepository.save(privateChatRoom);
    }
}
