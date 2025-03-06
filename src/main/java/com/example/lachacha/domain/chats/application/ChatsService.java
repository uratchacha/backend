package com.example.lachacha.domain.chats.application;

import com.example.lachacha.domain.chats.domain.ChatRoomRepository;
import com.example.lachacha.domain.chats.domain.PrivateChatRoom;
import com.example.lachacha.domain.chats.dto.ChatsMessageDto;
import com.example.lachacha.domain.chats.dto.request.PrivateChatsRequestDto;
import com.example.lachacha.domain.chats.exception.ChatsException;
import com.example.lachacha.domain.user.application.UsersService;
import com.example.lachacha.domain.user.domain.Users;
import com.example.lachacha.global.exception.MyErrorCode;
import com.example.lachacha.global.webSocket.chats.ChatHandler;
import com.example.lachacha.global.webSocket.notifications.NotificationHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.TextMessage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatsService
{
    private final NotificationHandler notificationHandler;
    private final ChatHandler chatHandler;
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
        notificationData.put("messageType", "request");
        notificationData.put("message", "새로운 채팅 요청이 왔습니다.");
        return mapper.writeValueAsString(notificationData);
    }

    @Transactional
    public void acceptChatRoom(PrivateChatsRequestDto chatsRequestDto) {
        Users requester = userService.findUsersById(chatsRequestDto.requesterId());
        Users receiver = userService.findUsersById(chatsRequestDto.receiverId());
        PrivateChatRoom privateChatRoom= PrivateChatRoom.builder()
                .user1(requester)
                .user2(receiver)
                .build();
        chatRoomRepository.save(privateChatRoom);
        // 채팅 승인 알림 전송
        sendChatNotification(requester.getId(), "채팅이 승인되었습니다.",privateChatRoom.getId());
    }

    public void rejectChatRoom(Long requesterId) {
        // 채팅 거절 알림 전송
        sendChatNotification(requesterId, "채팅이 거부되었습니다.",null);
    }


    private void sendChatNotification(Long userId, String messageBody, Long chatRoomId) {
        try {
            String notificationMessage = responseNotificationJson(messageBody,chatRoomId);
            notificationHandler.sendNotification(userId, notificationMessage);
        } catch (IOException e) {
            throw new ChatsException(MyErrorCode.NOTIFICATION_ERROR);
        }
    }

    private String responseNotificationJson(String messageBody,Long chatRoomId) throws JsonProcessingException
    {
        Map<String, Object> notificationData = new HashMap<>();
        notificationData.put("message", messageBody);

        if(chatRoomId!=null) {
            notificationData.put("chatRoomId", chatRoomId);
            notificationData.put("messageType", "accept");
        }

        else
            notificationData.put("messageType", "reject");

        return mapper.writeValueAsString(notificationData);
    }
    public void sendChatMessage(ChatsMessageDto chatMessageDto) {
        try {
            String message= createMessageJson(chatMessageDto);
            chatHandler.handleTextMessage(chatMessageDto.chatRoomId(), message);
        } catch (Exception e) {
            throw new ChatsException(MyErrorCode.NOTIFICATION_ERROR);
        }
    }

    private String createMessageJson(ChatsMessageDto chatMessageDto) throws JsonProcessingException
    {
        Map<String, Object> notificationData = new HashMap<>();
        notificationData.put("createTime", chatMessageDto.createTime());
        notificationData.put("message", chatMessageDto.message());
        notificationData.put("senderName", chatMessageDto.senderName());
        return mapper.writeValueAsString(notificationData);
    }
}
