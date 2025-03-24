package com.example.lachacha.global.firebase;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;

@Service
public class FirebaseInitializer {

    @PostConstruct
    public void initialize() {
        try {
            // 🔹 ClassPathResource를 사용해 JSON 파일을 로드 (경로 문제 해결)
            InputStream serviceAccount = new ClassPathResource("firebase-service-account.json").getInputStream();

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            // 🔹 Firebase가 이미 초기화되지 않았다면 실행
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                System.out.println("🔥 Firebase 초기화 완료");
            } else {
                System.out.println("⚡ Firebase는 이미 초기화되었습니다.");
            }

        } catch (IOException e) {
            System.err.println("❌ Firebase 초기화 실패: " + e.getMessage());
        }
    }
}
