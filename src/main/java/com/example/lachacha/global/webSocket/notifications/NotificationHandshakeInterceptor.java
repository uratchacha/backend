package com.example.lachacha.global.webSocket.notifications;

import com.example.lachacha.global.auth.jwt.TokenProvider;
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
public class NotificationHandshakeInterceptor implements HandshakeInterceptor
{
    private final TokenProvider tokenProvider;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,Map<String, Object> attributes)
    {
        if (request instanceof ServletServerHttpRequest servletRequest) {
            String token =servletRequest.getServletRequest().getHeader("Authorization");


            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
                Long userId = tokenProvider.getUserId(token);
                attributes.put("userId", userId);
            }
        }
        return true;
    }
    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
    }
}
