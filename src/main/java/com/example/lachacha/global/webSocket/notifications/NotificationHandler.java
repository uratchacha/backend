package com.example.lachacha.global.webSocket.notifications;

import com.mysql.cj.Session;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
@Slf4j
@Component
public class NotificationHandler extends TextWebSocketHandler {
    private static final Map<Long, WebSocketSession> userSessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        Long userId = getUserId(session);
        userSessions.put(userId, session);
        log.info("{} WebSocket 연결됨", userId);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        Long userId = getUserId(session);
        userSessions.remove(userId);
        log.info("{} WebSocket 연결 종료", userId);
    }

    // ✅ WebSocket을 통해 특정 사용자에게 알림 보내기 (상황별 메시지 설정)
    public void sendNotification(Long userId, String message) throws IOException {
        WebSocketSession session = userSessions.get(userId);
        if (session != null && session.isOpen()) {
            session.sendMessage(new TextMessage(message));
        }
    }

    // ✅ 알림 타입별 메시지 정의
    private String getMessageForType(String notificationType) {
        return switch (notificationType) {
            case "MATCH_REQUEST" -> "매칭 신청: 3분 내로 수락 여부를 알려주지 않으면 자동 취소됩니다.";
            case "MATCH_ACCEPTED" -> "매칭 완료: 매칭 요청이 수락되었습니다.";
            case "GROUP_MATCH" -> "그룹 매칭 완료: 정원이 모집되어 채팅방이 개설되었습니다.";
            default -> "새로운 알림이 도착했습니다.";
        };
    }

    private Long getUserId(WebSocketSession session) {
        Object userId = session.getAttributes().get("userId");
        return userId != null ? Long.parseLong(userId.toString()) : 1L;
    }


    public Map<Long, WebSocketSession> getUserSessions() {
            return userSessions;
    }

    public void notifyConferenceEnd(Long userId) throws IOException {
        WebSocketSession session = userSessions.get(userId);
        if (session != null && session.isOpen()) {
            session.sendMessage(new TextMessage("금일 네트워킹이 종료되었어요, 내일 다시 진행해주세요"));
            session.close(); // ✅ 채팅방 강제 종료 (세션 닫기)
        }
    }

}
