package com.example.lachacha.global.webSocket.chats;

import com.example.lachacha.domain.chats.dto.ChatsMessageDto;
import com.example.lachacha.domain.chats.exception.ChatsException;
import com.example.lachacha.global.exception.MyErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
@Slf4j
@RequiredArgsConstructor
public class ChatHandler extends TextWebSocketHandler
{
    private final ObjectMapper mapper;
    private static final Map<Long, Set<WebSocketSession>> rooms = new HashMap<>();


    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("{} 연결됨", session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

        String payload = message.getPayload();
        log.info("받은 메시지: {}", payload);
        ChatsMessageDto chatMessageDto =mapper.readValue(payload, ChatsMessageDto.class);
        log.info(chatMessageDto.toString());
        Long roomId= chatMessageDto.chatRoomId();
        String chatMessage = chatMessageDto.message();

        if (roomId == null || chatMessage == null) {
            throw new ChatsException(MyErrorCode.INVALID_MESSAGE_TYPE);
        }

        rooms.putIfAbsent(roomId, new HashSet<>());
        Set<WebSocketSession> roomSessions = rooms.get(roomId);

        roomSessions.add(session);

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status)  {

    }
}
