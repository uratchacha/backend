package com.example.lachacha.global.kafka;
import com.example.lachacha.global.webSocket.chats.ChatHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

@Slf4j
@Component
public class ChatKafkaConsumer {

    private final ChatHandler chatHandler;
    private final Queue<String> messageQueue = new ConcurrentLinkedQueue<>();
    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

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
        int queueSize = messageQueue.size();
        int batchSize = Math.min(queueSize / 5, 20);
        batchSize = Math.max(batchSize, 5);
        int finalBatchSize = batchSize;
        executorService.scheduleAtFixedRate(() -> {
            List<String> batch = new ArrayList<>();
            for (int i = 0; i < finalBatchSize && !messageQueue.isEmpty(); i++) {
                batch.add(messageQueue.poll());
            }

            if (batch.isEmpty()) return;

            for (String payload : batch) {
                try {
                    String[] data = payload.split(":", 2);
                    Long chatRoomId = Long.parseLong(data[0]);
                    String message = filterProfanity(data[1]);

                    chatHandler.broadcastMessage(chatRoomId, message);
                } catch (Exception e) {
                    log.error("메시지 처리 중 오류 발생", e);
                }
            }
        }, 0, 500, TimeUnit.MILLISECONDS);
    }

    private static final Pattern PROFANITY_PATTERN = Pattern.compile("시발|개새끼|병신");

    private String filterProfanity(String message) {
        return PROFANITY_PATTERN.matcher(message).replaceAll(match -> "*".repeat(match.group().length()));
    }
}