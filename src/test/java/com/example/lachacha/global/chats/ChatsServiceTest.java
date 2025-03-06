package com.example.lachacha.global.chats;


import com.example.lachacha.domain.chats.application.ChatsService;
import com.example.lachacha.domain.user.application.UsersService;
import com.example.lachacha.domain.user.domain.Users;
import com.example.lachacha.global.auth.jwt.JwtProperties;
import com.example.lachacha.global.auth.jwt.TokenProvider;
import com.example.lachacha.global.webSocket.notifications.NotificationHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ChatsServiceTest
{

    @Mock
    private NotificationHandler notificationHandler;

    @Mock
    private UsersService usersService;
    @InjectMocks
    private ChatsService chatsService;

    @BeforeEach
    void setUp()
    {
    }

    @Test
    void requestChatRoom_알림전송_테스트() throws Exception
    {
        Long requesterId = 1L;
        Long receiverId = 2L;


        Users requesterUser = Users.builder().id(requesterId).username("sdssdsd").password("sdsdd").build();
        when(usersService.findUsersById(requesterId)).thenReturn(requesterUser);


        chatsService.requestChatRoom(requesterId, receiverId);
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(notificationHandler).sendNotification(Mockito.eq(receiverId), messageCaptor.capture());

        // JSON 메시지가 올바르게 생성되었는지 확인
        String sentMessage = messageCaptor.getValue();
        System.out.println("전송된 메시지: " + sentMessage);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(sentMessage);

        assertEquals(requesterId, jsonNode.get("requesterId").asLong());
        assertEquals(receiverId, jsonNode.get("receiverId").asLong());
        assertEquals("새로운 채팅 요청이 왔습니다.", jsonNode.get("message").asText());
    }
}
