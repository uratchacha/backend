package com.example.lachacha.global.webSocket.notifications;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

import static org.mockito.Mockito.*;

class NotificationHandlerTest {

    @Mock
    private WebSocketSession session;

    private NotificationHandler notificationHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        notificationHandler = new NotificationHandler();
    }

    @Test
    void sendNotification_ShouldSendMessageToWebSocketSession() throws IOException {
        Long userId = 123L;
        String message = "Test WebSocket Notification";

        when(session.isOpen()).thenReturn(true);
        notificationHandler.getUserSessions().put(userId, session); // getter 사용

        notificationHandler.sendNotification(userId, message);

        verify(session, times(1)).sendMessage(any(TextMessage.class));
    }
}
