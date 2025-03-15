package com.example.lachacha.global.kafka;


import com.example.lachacha.global.webSocket.chats.ChatHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class ChatKafkaConsumer {

    private final ChatHandler chatHandler;
    private final List<String> messageQueue = new ArrayList<>();
    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

    public ChatKafkaConsumer(ChatHandler chatHandler) {
        this.chatHandler = chatHandler;
        startMessageProcessing();
    }
    @KafkaListener(topics = "chat-messages", groupId = "chat-group")
    public void listen(String payload) {
        log.info("Kafka 메시지 수신: {}", payload);
        try {
            synchronized (messageQueue) {
                messageQueue.add(payload); // 메시지를 큐에 추가
            }
        } catch (Exception e) {
            log.error("Kafka 메시지 처리 중 오류 발생", e);
        }
    }
    private void startMessageProcessing() {
        executorService.scheduleAtFixedRate(() -> {
            List<String> batch;
            synchronized (messageQueue) {
                if (messageQueue.isEmpty()) return;
                batch = new ArrayList<>(messageQueue.subList(0, Math.min(10, messageQueue.size()))); // 최대 10개씩 처리
                messageQueue.removeAll(batch);
            }

            for (String payload : batch) {
                try {
                    String[] data = payload.split(":", 2);
                    Long chatRoomId = Long.parseLong(data[0]);
                    String message = data[1];

                    String filteredMessage = filterProfanity(message);

                    // 기존 chatHandler로 메시지 전송
                    chatHandler.broadcastMessage(chatRoomId, filteredMessage);
                    Thread.sleep(100); // 100ms 딜레이 추가
                } catch (Exception e) {
                    log.error("메시지 처리 중 오류 발생", e);
                }
            }
        }, 0,800, TimeUnit.MILLISECONDS); // 200ms마다 실행
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
