package com.example.lachacha.domain.chats.application;

import com.example.lachacha.domain.chats.domain.ChatRoom;
import com.example.lachacha.domain.chats.domain.ChatRoomRepository;
import com.example.lachacha.domain.chats.domain.GroupChatRoom;
import com.example.lachacha.domain.chats.domain.PrivateChatRoom;
import com.example.lachacha.domain.chats.dto.ChatsMessageDto;
import com.example.lachacha.domain.chats.dto.request.GroupChatsRequestDto;
import com.example.lachacha.domain.chats.dto.request.JoinGroupRequestDto;
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

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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

    private final Map<Long, Lock> userLockMap = new HashMap<>();
    private final Map<Long, Lock> chatRoomLockMap = new HashMap<>();


    private Lock getUserLock(Long userId) {
        return userLockMap.computeIfAbsent(userId, k -> new ReentrantLock());
    }

    private Lock getChatRoomLock(Long chatRoomId) {
        return userLockMap.computeIfAbsent(chatRoomId, k -> new ReentrantLock());
    }


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
    public Long acceptChatRoom(PrivateChatsRequestDto chatsRequestDto) {
        Users requester = userService.findUsersById(chatsRequestDto.requesterId());
        Users receiver = userService.findUsersById(chatsRequestDto.receiverId());

        Lock firstLock = getUserLock(Math.min(requester.getId(), receiver.getId()));
        Lock secondLock = getUserLock(Math.max(requester.getId(), receiver.getId()));

        PrivateChatRoom privateChatRoom;

        firstLock.lock();
        try {
            secondLock.lock();
            try {
                if (requester.getChatRoom() != null || receiver.getChatRoom() != null) {
                    throw new ChatsException(MyErrorCode.ALREADY_IN_CHAT);
                }

                privateChatRoom = PrivateChatRoom.builder()
                        .maxSize(2)
                        .build();
                privateChatRoom.addMember(requester);
                privateChatRoom.addMember(receiver);
                chatRoomRepository.save(privateChatRoom);

                if (requester.getId() < receiver.getId()) {
                    receiver.setChatRoom(privateChatRoom);
                } else {
                    requester.setChatRoom(privateChatRoom);
                }
            } finally {
                secondLock.unlock();
            }
            if (requester.getId() < receiver.getId()) {
                requester.setChatRoom(privateChatRoom);
            } else {
                receiver.setChatRoom(privateChatRoom);
            }

        } finally {
            firstLock.unlock();
        }

        // 채팅 승인 알림 전송
        sendChatNotification(requester.getId(), "채팅이 승인되었습니다.",privateChatRoom.getId());
        return privateChatRoom.getId();
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
        notificationData.put("createTime", chatMessageDto.createTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        notificationData.put("message", chatMessageDto.message());
        notificationData.put("senderName", chatMessageDto.senderName());
        return mapper.writeValueAsString(notificationData);
    }

    public Long createGroupChat(GroupChatsRequestDto groupChatsRequestDto)
    {
        GroupChatRoom groupChatRoom = GroupChatRoom.builder().maxSize(groupChatsRequestDto.maxSize()).build();
        Users requestUser = userService.findUsersById(groupChatsRequestDto.userId());

        groupChatRoom.addMember(requestUser);
        requestUser.setChatRoom(groupChatRoom);

        chatRoomRepository.save(groupChatRoom);

        return groupChatRoom.getId();
    }

    public Long joinGroupChat(JoinGroupRequestDto joinGroupRequestDto)
    {
        GroupChatRoom groupChatRoom= (GroupChatRoom)
                chatRoomRepository.findById(joinGroupRequestDto.chatRoomId()).orElseThrow();
        Users requestUser = userService.findUsersById(joinGroupRequestDto.userId());
        Lock chatRoomLock = getChatRoomLock(joinGroupRequestDto.chatRoomId());


        chatRoomLock.lock();
        try {
            groupChatRoom.addMember(requestUser);
            requestUser.setChatRoom(groupChatRoom);
        } finally {
            chatRoomLock.unlock();
        }

        return groupChatRoom.getId();
    }
}
