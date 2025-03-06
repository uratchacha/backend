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
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatsService
{
    private final NotificationHandler notificationHandler;
    private final ChatRoomRepository chatRoomRepository;
    private final UsersService userService;
    private final ObjectMapper mapper=new ObjectMapper();
    public void requestChatRoom(Long requesterId, Long receiverId)
    {
        try {
            String notificationMessage = createRequestNotificationJson(requesterId,receiverId);
            notificationHandler.sendNotification(receiverId, notificationMessage);
        } catch (IOException e) {
            throw new ChatsException(MyErrorCode.NOTIFICATION_ERROR);
        }
    }

    private String createRequestNotificationJson(Long requesterId, Long receiverId) throws JsonProcessingException
    {
        Map<String, Object> notificationData = new HashMap<>();
        Users requesterUser = userService.findUsersById(requesterId);
        notificationData.put("requesterUser", requesterUser);
        notificationData.put("requesterId", requesterId);
        notificationData.put("receiverId", receiverId);
        notificationData.put("message", "새로운 채팅 요청이 왔습니다.");
        return mapper.writeValueAsString(notificationData);
    }

    private String createResponseNotificationJson() throws JsonProcessingException
    {
        Map<String, Object> notificationData = new HashMap<>();
        notificationData.put("message", "채팅이 승낙되었습니다.");
        return mapper.writeValueAsString(notificationData);
    }
    public void acceptChatRoom(PrivateChatsRequestDto chatsRequestDto) {
        Users requester = userService.findUsersById(chatsRequestDto.requesterId());
        Users receiver = userService.findUsersById(chatsRequestDto.receiverId());
        PrivateChatRoom privateChatRoom= PrivateChatRoom.builder()
                .user1(requester)
                .user2(receiver)
                .build();

        try {
            String acceptResponseMessage = createResponseNotificationJson();
            notificationHandler.sendNotification(requester.getId(), acceptResponseMessage);
        } catch (IOException e) {
            throw new ChatsException(MyErrorCode.NOTIFICATION_ERROR);
        }

        chatRoomRepository.save(privateChatRoom);
    }
}
