package com.example.lachacha.global.kafka;
import com.example.lachacha.global.webSocket.chats.ChatHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.regex.Pattern;

@Slf4j
@Component
public class ChatKafkaConsumer {

    private final ChatHandler chatHandler;

    public ChatKafkaConsumer(ChatHandler chatHandler) {
        this.chatHandler = chatHandler;
    }

    @KafkaListener(
            topics = "chat-messages",
            groupId = "chat-group",
            containerFactory = "kafkaBatchListenerContainerFactory"
    )    public void listen(List<String> payloads, Acknowledgment acknowledgment) {
        log.info("Batch 메시지 수신: {}건", payloads.size());

        for (String payload : payloads) {
            try {
                processMessage(payload);
            } catch (Exception e) {
                log.error("Kafka 메시지 처리 중 오류 발생: {}", payload, e);
                // 실패한 메시지를 별도로 저장하거나 DLQ(Dead Letter Queue)로 보내는 로직 추가 가능
            }
        }

        acknowledgment.acknowledge();
    }


    private void processMessage(String payload) {
        try {
            String[] data = payload.split(":", 2);
            Long chatRoomId = Long.parseLong(data[0]);
            String message = filterProfanity(data[1]);

            chatHandler.broadcastMessage(chatRoomId, message);
        } catch (Exception e) {
            log.error("메시지 처리 중 오류 발생, 재시도: {}", payload, e);

        }
    }


    private static final Pattern PROFANITY_PATTERN = Pattern.compile("시발|개새끼|병신");

    private String filterProfanity(String message) {
        return PROFANITY_PATTERN.matcher(message).replaceAll(match -> "*".repeat(match.group().length()));
    }
}