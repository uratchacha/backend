package com.example.lachacha.global.webSocket.chats;


import com.example.lachacha.global.kafka.ChatProducer;
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
    private final ChatProducer chatProducer;
    private static final Map<Long, Set<WebSocketSession>> rooms = new HashMap<>();


    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        Long chatRoomId = getChatRoomId(session);
        rooms.computeIfAbsent(chatRoomId, k -> new HashSet<>()).add(session);
        log.info("{} 연결됨", session.getId());
    }


    public void handleTextMessage(Long chatRoomId, String message) {
        log.info("사용자로부터 메시지 수신: {}", message);
        chatProducer.sendMessage(chatRoomId, message);

    }

    public void broadcastMessage(Long chatRoomId, String message) throws IOException {
        Set<WebSocketSession> sessions = rooms.get(chatRoomId);
        if (sessions == null) return;

        for (WebSocketSession session : sessions) {
            sendMessage(session, message);
        }
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
