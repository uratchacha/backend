package com.example.lachacha.global.kafka;
import com.example.lachacha.global.webSocket.chats.ChatHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
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
    private final Queue<String> messageQueue = new ConcurrentLinkedQueue<>();
    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(
            Runtime.getRuntime().availableProcessors() * 2);
    private static final int MAX_RETRY_COUNT = 3;
    private final Queue<String> failedMessages = new ConcurrentLinkedQueue<>();
    private final Map<String, Integer> retryCountMap = new ConcurrentHashMap<>();

    public ChatKafkaConsumer(ChatHandler chatHandler) {
        this.chatHandler = chatHandler;
        startMessageProcessing();
    }

    @KafkaListener(topics = "chat-messages", groupId = "chat-group", concurrency = "6")
    public void listen(String payload) {
        try {
            messageQueue.add(payload);
        } catch (Exception e) {
            log.error("Kafka 메시지 처리 중 오류 발생", e);
        }
    }

    private void startMessageProcessing() {
        executorService.scheduleAtFixedRate(() -> {
            List<String> batch = new ArrayList<>();
            int batchSize = Math.max(Math.min(messageQueue.size() / 5, 20), 5);

            for (int i = 0; i < batchSize && !messageQueue.isEmpty(); i++) {
                batch.add(messageQueue.poll());
            }

            if (batch.isEmpty()) return;

            for (String payload : batch) {
                processMessage(payload);
            }

            retryFailedMessages(); // 실패한 메시지 재처리
        }, 0, 500, TimeUnit.MILLISECONDS);
    }

    private void processMessage(String payload) {
        try {
            String[] data = payload.split(":", 2);
            Long chatRoomId = Long.parseLong(data[0]);
            String message = filterProfanity(data[1]);

            chatHandler.broadcastMessage(chatRoomId, message);
        } catch (Exception e) {
            log.error("메시지 처리 중 오류 발생, 재시도: {}", payload, e);
            retryMessage(payload);
        }
    }

    private void retryMessage(String payload) {
        int retryCount = retryCountMap.getOrDefault(payload, 0);

        if (retryCount <= MAX_RETRY_COUNT) {
            retryCountMap.put(payload, retryCount + 1);
            failedMessages.add(payload);
        } else {
            log.error("메시지 재시도 횟수 초과, 폐기됨: {}", payload);
            retryCountMap.remove(payload);
        }
    }

    private void retryFailedMessages() {
        List<String> retryBatch = new ArrayList<>();
        int batchSize = Math.max(Math.min(failedMessages.size() / 5, 10), 3);

        for (int i = 0; i < batchSize && !failedMessages.isEmpty(); i++) {
            retryBatch.add(failedMessages.poll());
        }

        for (String payload : retryBatch) {
            processMessage(payload);
        }
    }

    private static final Pattern PROFANITY_PATTERN = Pattern.compile("시발|개새끼|병신");

    private String filterProfanity(String message) {
        return PROFANITY_PATTERN.matcher(message).replaceAll(match -> "*".repeat(match.group().length()));
    }
}