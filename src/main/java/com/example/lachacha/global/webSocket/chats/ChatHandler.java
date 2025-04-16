package com.example.lachacha.global.webSocket.chats;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Pattern;

@Slf4j
//@RequiredArgsConstructor
@Component
public class ChatHandler extends TextWebSocketHandler
{
    //private final ChatProducer chatProducer;
    private static final Map<Long, Set<WebSocketSession>> rooms = new HashMap<>();
    private static final Map<Long, WebSocketSession> userSessions = new ConcurrentHashMap<>();

    private final Queue<String> messageQueue = new ConcurrentLinkedQueue<>();
    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors() * 2);

    public ChatHandler() {
        startBatchProcessing();
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        Long chatRoomId = getChatRoomId(session);
        Long userId= getUserId(session);
        userSessions.put(userId, session);
        rooms.computeIfAbsent(chatRoomId, k -> new HashSet<>()).add(session);
        log.info("{} 연결됨", session.getId());
        log.info("{} 유저 연결됨", userId);
    }


    public void handleTextMessage(Long chatRoomId, String message) throws IOException {
        log.info("사용자로부터 메시지 수신: {}", message);
        String payload = chatRoomId + ":" + message; // 메시지에 채팅방 ID 포함
        messageQueue.add(payload);
    }

    private void startBatchProcessing() {
        int queueSize = messageQueue.size();
        int batchSize = Math.min(queueSize / 5, 20);
        batchSize = Math.max(batchSize, 5);
        int finalBatchSize = batchSize;
        executorService.scheduleAtFixedRate(() -> {
            List<String> batch = new ArrayList<>();
            for (int i = 0; i < finalBatchSize && !messageQueue.isEmpty(); i++) {
                batch.add(messageQueue.poll());
            }

            if (batch.isEmpty()) return;

            for (String payload : batch) {
                try {
                    String[] data = payload.split(":", 2);
                    Long chatRoomId = Long.parseLong(data[0]);
                    String message = filterProfanity(data[1]);
                    log.info("chatRoomId: {}, message: {}", chatRoomId, message);
                    broadcastMessage(chatRoomId, message);
                } catch (Exception e) {
                    log.error("메시지 처리 중 오류 발생", e);
                }
            }
        }, 0, 500, TimeUnit.MILLISECONDS);
    }

    private static final Pattern PROFANITY_PATTERN = Pattern.compile("시발|개새끼|병신");

    private String filterProfanity(String message) {
        return PROFANITY_PATTERN.matcher(message).replaceAll(match -> "*".repeat(match.group().length()));
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
    public void exitRoom(Long chatRoomId, Long userId)
    {
        WebSocketSession session = userSessions.get(userId);
        if (session != null) {
            Set<WebSocketSession> sessions = rooms.get(chatRoomId);
            if (sessions != null) {
                sessions.remove(session);
            }
        }
        log.info("{} 유저가 채팅방에서 나갔습니다.", userId);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status)  {
        Long chatRoomId = getChatRoomId(session);
        Long userId= getUserId(session);
        rooms.get(chatRoomId).remove(session);
        userSessions.remove(userId);
        log.info("{} 채팅방에서 나갔습니다.", chatRoomId);
    }

    private Long getChatRoomId(WebSocketSession session) {
        return Long.parseLong(session.getAttributes().get("chatRoomId").toString());
    }
    private Long getUserId(WebSocketSession session) {

        //테스트를 위해 임시적으로 변환
        Object userId = session.getAttributes().get("userId");
        return userId != null ? Long.parseLong(userId.toString()) : 1L;
    }

    public synchronized Map<Long, Set<WebSocketSession>> getRooms() {
        return rooms;
    }

}
