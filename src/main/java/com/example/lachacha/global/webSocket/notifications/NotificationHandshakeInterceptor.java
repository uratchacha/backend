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
            // ✅ JWT 인증 토큰 파싱
            String token = servletRequest.getServletRequest().getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
                try {
                    Long userId = tokenProvider.getUserId(token);
                    attributes.put("userId", userId);
                } catch (Exception e) {
                    System.out.println("⚠️ JWT 인증 실패: " + e.getMessage());
                }
            }

            // ✅ 쿼리 파라미터 방식도 병행 지원, 테스트에 용이함
            String userIdParam = servletRequest.getServletRequest().getParameter("userId");
            String notificationType = servletRequest.getServletRequest().getParameter("notificationType");

            if (userIdParam != null) {
                try {
                    attributes.put("userId", Long.parseLong(userIdParam));
                } catch (NumberFormatException e) {
                    System.out.println("⚠️ userId 파라미터 형식 오류");
                }
            }

            if (notificationType != null) {
                attributes.put("notificationType", notificationType);
            }
        }

        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
    }
}
