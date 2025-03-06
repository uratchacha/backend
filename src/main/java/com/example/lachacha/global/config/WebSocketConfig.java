package com.example.lachacha.global.config;

import com.example.lachacha.global.webSocket.chats.ChatHandler;
import com.example.lachacha.global.webSocket.chats.ChatHandshakeInterceptor;
import com.example.lachacha.global.webSocket.notifications.NotificationHandler;
import com.example.lachacha.global.webSocket.notifications.NotificationHandshakeInterceptor;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {


    private final NotificationHandshakeInterceptor notificationHandshakeInterceptor;
    private final ChatHandshakeInterceptor chatHandshakeInterceptor;
    private final ObjectMapper objectMapper;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new ChatHandler(), "/chats")
                .setAllowedOrigins("*")
                .addInterceptors(chatHandshakeInterceptor);


        registry.addHandler(new NotificationHandler(), "/notifications")
                .setAllowedOrigins("*")
                .addInterceptors(notificationHandshakeInterceptor);
    }
}
