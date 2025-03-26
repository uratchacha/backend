package com.example.lachacha.global.webSocket.notifications;

import com.example.lachacha.global.auth.jwt.TokenProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationHandshakeInterceptor implements HandshakeInterceptor
{
    private final TokenProvider tokenProvider;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,Map<String, Object> attributes)
    {
        if (request instanceof ServletServerHttpRequest servletRequest) {

            String accessToken = servletRequest.getServletRequest().getParameter("access_token");

            if (accessToken != null) {
                Long userId = tokenProvider.getUserId(accessToken);
                log.info("user id is {}", userId);
                attributes.put("userId", userId);
            }


            return true;
        }

        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
    }
}
