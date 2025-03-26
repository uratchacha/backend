package com.example.lachacha.global.webSocket.chats;

import com.example.lachacha.global.auth.jwt.TokenProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class ChatHandshakeInterceptor implements HandshakeInterceptor
{
    private final TokenProvider tokenProvider;
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes)
    {
        if (request instanceof ServletServerHttpRequest servletRequest) {
            String chatRoomId = servletRequest.getServletRequest().getParameter("chatRoomId");
            String accessToken = servletRequest.getServletRequest().getParameter("access_token");

            if (accessToken != null) {
                Long userId = tokenProvider.getUserId(accessToken);
                attributes.put("userId", userId);
            }

            if (chatRoomId != null) {
                attributes.put("chatRoomId", chatRoomId);
            }

        return true;
        }
        return false;
    }
    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception)
    {
    }
}
