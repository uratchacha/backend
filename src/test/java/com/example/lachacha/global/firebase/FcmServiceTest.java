package com.example.lachacha.global.firebase;
import com.google.api.core.SettableApiFuture;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.auth.oauth2.GoogleCredentials;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;

import static org.mockito.Mockito.*;

class FcmServiceTest {

    @InjectMocks
    private FcmService fcmService;

    @Mock
    private FirebaseMessaging firebaseMessaging;

    @BeforeEach
    void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);

        // ✅ FirebaseApp 초기화 (테스트 환경)
        if (FirebaseApp.getApps().isEmpty()) {
            InputStream serviceAccount = new ClassPathResource("firebase-service-account.json").getInputStream();

            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();
            FirebaseApp.initializeApp(options);
        }
    }

    @Test
    void sendPushNotification_ShouldSendNotificationSuccessfully() throws ExecutionException, InterruptedException {
        String token = "test_fcm_token"; // ✅ 테스트용 가짜 FCM 토큰
        String title = "Test Notification";
        String body = "This is a test notification.";

        // ✅ Mock FCM 응답을 생성
        SettableApiFuture<String> mockResponse = SettableApiFuture.create();
        mockResponse.set("mockResponse");

        // ✅ Mock 설정이 올바르게 적용되었는지 확인
        when(firebaseMessaging.sendAsync(any(Message.class))).thenReturn(mockResponse);

        fcmService.sendPushNotification(token, title, body);

        // ✅ sendAsync()가 1회 호출되었는지 검증
        verify(firebaseMessaging, times(1)).sendAsync(any(Message.class));
    }

    @Test
    void sendPushNotificationByUserId_ShouldRetrieveTokenAndSendNotification() throws ExecutionException, InterruptedException {
        Long userId = 123L;
        String token = "test_fcm_token"; // ✅ 테스트용 가짜 FCM 토큰
        String title = "User Notification";
        String body = "This is a notification for user.";

        fcmService.saveFcmToken(userId, token);

        SettableApiFuture<String> mockResponse = SettableApiFuture.create();
        mockResponse.set("mockResponse");

        when(firebaseMessaging.sendAsync(any(Message.class))).thenReturn(mockResponse);

        fcmService.sendPushNotificationByUserId(userId, title, body);

        verify(firebaseMessaging, times(1)).sendAsync(any(Message.class));
    }
}
