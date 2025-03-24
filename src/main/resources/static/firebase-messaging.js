import { initializeApp } from "https://www.gstatic.com/firebasejs/9.6.1/firebase-app.js";
import { getMessaging, getToken, onMessage } from "https://www.gstatic.com/firebasejs/9.6.1/firebase-messaging.js";

// ✅ Firebase 설정중
const firebaseConfig = {
    apiKey: "AIzaSyAG5Dy_cMVsKyp9mrF6X8mWYa5xM_16IzI",
    authDomain: "uratchacha-9c430.firebaseapp.com",
    projectId: "uratchacha-9c430",
    storageBucket: "uratchacha-9c430.firebasestorage.app",
    messagingSenderId: "975825146714",
    appId: "1:975825146714:web:e338b5e9ae8e67264f1963",
    measurementId: "G-FM9MG74NEC"
};
// ✅ Firebase 앱 초기화
const app = initializeApp(firebaseConfig);
const messaging = getMessaging(app);

// ✅ 사용자 ID 가져오는 함수
async function getUserId() {
    // 1️⃣ localStorage에서 사용자 ID 가져오기
    let userId = localStorage.getItem("userId");

    // 2️⃣ localStorage에 없으면 백엔드에서 가져오기
    if (!userId) {
        try {
            const response = await fetch("/api/users/me"); // 백엔드에서 현재 사용자 ID 가져오기
            if (!response.ok) throw new Error("사용자 정보를 가져올 수 없음.");

            const data = await response.json();
            userId = data.userId;
            localStorage.setItem("userId", userId); // 가져온 ID를 localStorage에 저장
        } catch (error) {
            console.error("❌ 사용자 ID 가져오기 실패:", error);
            return null;
        }
    }
    return userId;
}

// ✅ 푸시 알림 권한 요청 및 FCM 토큰 받아 백엔드에 저장
async function requestNotificationPermission() {
    const userId = await getUserId();
    if (!userId) {
        console.warn("❌ 사용자 ID가 없어서 FCM 등록 불가.");
        return;
    }

    const permission = await Notification.requestPermission();
    if (permission === "granted") {
        console.log("✅ 알림 권한이 허용됨.");
        const token = await getToken(messaging, { vapidKey: "BLCkdOajIfp0UETEtTgbwCUfHiWto410V9k0mrCFLqO3OIdjW7GbOTUKS8Jwiffta5olcFRLDLxCqRT1zaPM2Yc" });
        console.log("📌 FCM 토큰:", token);
        sendTokenToServer(userId, token);
    } else {
        console.warn("❌ 알림 권한이 거부됨.");
    }
}

// ✅ FCM 토큰을 백엔드로 전송하는 함수
async function sendTokenToServer(userId, token) {
    try {
        const response = await fetch("/api/notifications/register-token", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ userId, token })
        });

        if (response.ok) {
            console.log("✅ FCM 토큰이 서버에 성공적으로 저장됨.");
        } else {
            console.error("❌ 서버에 FCM 토큰 저장 실패:", response.statusText);
        }
    } catch (error) {
        console.error("❌ 서버 요청 중 오류 발생:", error);
    }
}

// ✅ WebSocket 연결 시 사용자 ID 포함
async function connectWebSocket() {
    const userId = await getUserId();
    if (!userId) {
        console.warn("❌ WebSocket 연결 실패: 사용자 ID가 없음.");
        return;
    }

    const notificationType = "MATCH_REQUEST"; // 예제 알림 타입
    const socket = new WebSocket(`ws://localhost:8080/notifications?userId=${userId}&notificationType=${notificationType}`);

    socket.onopen = () => {
        console.log("✅ WebSocket 연결됨!");
    };

    socket.onmessage = (event) => {
        console.log("📩 WebSocket 알림 수신:", event.data);
        alert("새로운 알림: " + event.data);
    };

    socket.onclose = () => {
        console.log("❌ WebSocket 연결 종료");
    };
}


async function registerFcmToken(userId, token) {
    await fetch("/api/notifications/register-token", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ userId, token })
    });
}

window.requestNotificationPermission = requestNotificationPermission;
