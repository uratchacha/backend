package com.example.lachacha.global.webSocket.chats;

import com.example.lachacha.domain.chats.dto.ChatsMessageDto;
import com.example.lachacha.domain.chats.exception.ChatsException;
import com.example.lachacha.global.exception.MyErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
@Slf4j
@RequiredArgsConstructor
@Component
public class ChatHandler extends TextWebSocketHandler
{
    private static final Map<Long, Set<WebSocketSession>> rooms = new HashMap<>();


    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        Long chatRoomId = getChatRoomId(session);
        rooms.computeIfAbsent(chatRoomId, k -> new HashSet<>()).add(session);
        log.info("{} 연결됨", session.getId());
    }


    public void handleTextMessage(Long chatRoomId, String message) throws Exception {
        Set<WebSocketSession> sessions = rooms.get(chatRoomId);
        if(sessions == null)
            throw new ChatsException(MyErrorCode.SESSION_NOT_FOUND);
        for (WebSocketSession session : sessions)
            sendMessage(session, message);

    }

    public void sendMessage(WebSocketSession session, String message) throws IOException {
        if (session != null && session.isOpen()) {
            session.sendMessage(new TextMessage(message));
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status)  {
        Long chatRoomId = getChatRoomId(session);
        rooms.get(chatRoomId).remove(session);
        log.info("{} 채팅방에서 나갔습니다.", chatRoomId);
    }

    private Long getChatRoomId(WebSocketSession session) {
        return Long.parseLong(session.getAttributes().get("chatRoomId").toString());
    }

}
