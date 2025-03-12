package com.example.lachacha.global.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChatProducer {
    private final KafkaTemplate<String,String> kafkaTemplate;
    private static final String TOPIC = "chat-messages";

    public void sendMessage(Long chatRoomId, String message) {
        String payload = chatRoomId + ":" + message; // 메시지에 채팅방 ID 포함
        kafkaTemplate.send(TOPIC, payload);
        log.info("Kafka로 메시지 전송: {}", payload);
    }
}