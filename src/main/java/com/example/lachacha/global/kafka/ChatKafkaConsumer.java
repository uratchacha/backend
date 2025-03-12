package com.example.lachacha.global.kafka;


import com.example.lachacha.global.webSocket.chats.ChatHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class ChatKafkaConsumer {

    private final ChatHandler chatHandler;

    @KafkaListener(topics = "chat-messages", groupId = "chat-group")
    public void listen(String payload) {
        log.info("Kafka 메시지 수신: {}", payload);
        try {
            String[] data = payload.split(":", 2);
            Long chatRoomId = Long.parseLong(data[0]);
            String message = data[1];

            String filteredMessage = filterProfanity(message);

            chatHandler.broadcastMessage(chatRoomId, filteredMessage);
        } catch (Exception e) {
            log.error("Kafka 메시지 처리 중 오류 발생", e);
        }
    }

    private String filterProfanity(String message) {
        // 욕설 리스트
        List<String> profanityList = Arrays.asList("시발", "개새끼", "병신"); // 실제 욕설 리스트

        // 욕설이 포함된 메시지를 필터링
        for (String profanity : profanityList) {
            if (message.contains(profanity)) {
                message = message.replaceAll(profanity, "*".repeat(profanity.length())); // 욕설을 ***로 교체
            }
        }
        return message;
    }
}
