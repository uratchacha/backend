package com.example.lachacha.global.webSocket.networkingTables;

import com.example.lachacha.global.webSocket.chats.ChatHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import java.io.IOException;
import java.time.Duration;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class TableWaitTimeHandler {

    private final ChatHandler chatHandler;
    // 예약 대기 중인 맵 (NOT_RESERVED 상태)
    private final Map<Long, Long> notReservedReservations = new LinkedHashMap<>(); // 채팅방 ID → 예약 ID 매핑
    // 예약이 확정된 맵 (RESERVED 상태)
    private final Map<Long, Long> reservedReservations = new LinkedHashMap<>(); // 채팅방 ID → 예약 ID 매핑

//    public void updateWaitTimes(List<Integer> waitTimes, Duration networkingDuration) {
//      //  List<Integer> waitTimes = networkingTableService.calculateEstimatedWaitTimes();
//        Map<Long, Set<WebSocketSession>> rooms = chatHandler.getRooms();
//
//        if (waitTimes.isEmpty()) {
//            for (Set<WebSocketSession> sessions : rooms.values()) {
//                for (WebSocketSession session : sessions) {
//                    sendWaitTime(session, 0);
//                }
//            }
//            return;
//        }
//
//        int i = 0, n = 0, lastWaitTime = 0;
//
//        for (Map.Entry<Long, Long> entry : reservedReservations.entrySet()) {
//            Long chatRoomId = entry.getKey();
//            int waitTime = waitTimes.get(n) + (int) networkingDuration.toMinutes() * i;
//
//            Optional.ofNullable(rooms.get(chatRoomId)).ifPresent(sessions -> {
//                for (WebSocketSession session : sessions) {
//                    sendWaitTime(session, waitTime);
//                }
//            });
//
//            n++;
//            if (n >= waitTimes.size()) {
//                n = 0;
//                i++;
//            }
//
//            lastWaitTime = waitTime;
//        }
//
//        for (Long chatRoomId : rooms.keySet()) {
//            if (!reservedReservations.containsKey(chatRoomId)) {
//                for (WebSocketSession session : rooms.get(chatRoomId)) {
//                    sendWaitTime(session, lastWaitTime);
//                }
//            }
//        }
//    }



    public Long findChatRoomIdByUsers(List<Long> userIds) {
        Map<Long, Set<WebSocketSession>> rooms = chatHandler.getRooms();

        for (Map.Entry<Long, Set<WebSocketSession>> entry : rooms.entrySet()) {
            Long chatRoomId = entry.getKey();
            Set<WebSocketSession> sessions = entry.getValue();

            // 모든 유저가 포함된 채팅방을 찾음
            boolean allUsersInChatRoom = userIds.stream()
                    .allMatch(userId -> sessions.stream()
                            .map(this::getUserIdFromSession)
                            .filter(Objects::nonNull)
                            .anyMatch(sessionUserId -> sessionUserId.equals(userId)));

            if (allUsersInChatRoom) {
                return chatRoomId;
            }
        }
        return null;
    }

    private Long getUserIdFromSession(WebSocketSession session) {
        Object userId = session.getAttributes().get("userId");
        if (userId instanceof String) {
            return Long.parseLong((String) userId);
        } else if (userId instanceof Long) {
            return (Long) userId;
        }
        return null;
    }

    // 특정 채팅방의 예약 ID 가져오기
    public Long getReservationIdByChatRoomId(Long chatRoomId) {
        // NOT_RESERVED에 존재하면 반환
        if (notReservedReservations.containsKey(chatRoomId)) {
            return notReservedReservations.get(chatRoomId);
        }
        // RESERVED에 존재하면 반환
        return reservedReservations.get(chatRoomId);
    }


    //맵에서 가장 첫 번째 예약의 채팅방 ID 찾기
    public Long getFirstChatRoomId() {
        return reservedReservations.keySet().stream().findFirst().orElse(null);
    }

    //예약이 배정되면 맵에서 삭제
    public void removeReservationById(Long reservationId) {
        reservedReservations.values().removeIf(id -> id.equals(reservationId));
    }


    // 예약이 생성되면 NOT_RESERVED 맵에 저장
    public void addNotReservedReservation(Long chatRoomId, Long reservationId) {
        notReservedReservations.put(chatRoomId, reservationId);
    }


    // 예약이 확정되면 NOT_RESERVED 맵에서 삭제 후 RESERVED 맵에 저장
    public void confirmReservation(Long chatRoomId, Long reservationId) {
        notReservedReservations.remove(chatRoomId);
        reservedReservations.put(chatRoomId, reservationId);
    }

    // 배정 완료된 예약을 RESERVED 맵에서 삭제
    public void completeReservation(Long chatRoomId) {
        reservedReservations.remove(chatRoomId);
    }

}