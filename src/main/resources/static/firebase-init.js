const firebaseConfig = {
    apiKey: "AIzaSyAG5Dy_cMVsKyp9mrF6X8mWYa5xM_16IzI",
    authDomain: "uratchacha-9c430.firebaseapp.com",
    projectId: "uratchacha-9c430",
    storageBucket: "uratchacha-9c430.firebasestorage.app",
    messagingSenderId: "975825146714",
    appId: "1:975825146714:web:e338b5e9ae8e67264f1963",
    measurementId: "G-FM9MG74NEC"
};

firebase.initializeApp(firebaseConfig);
const messaging = firebase.messaging();

async function getUserId() {
    let userId = localStorage.getItem("userId");
    if (!userId) {
        try {
            const response = await fetch("/api/users/me");
            if (!response.ok) throw new Error("사용자 정보를 가져올 수 없음.");
            const data = await response.json();
            userId = data.userId;
            localStorage.setItem("userId", userId);
        } catch (error) {
            console.error("❌ 사용자 ID 가져오기 실패:", error);
            return null;
        }
    }
    return userId;
}

async function requestNotificationPermission() {
    const userId = await getUserId();
    if (!userId) return;

    const permission = await Notification.requestPermission();
    if (permission === "granted") {
        const token = await messaging.getToken({
            vapidKey: "BLCkdOajIfp0UETEtTgbwCUfHiWto410V9k0mrCFLqO3OIdjW7GbOTUKS8Jwiffta5olcFRLDLxCqRT1zaPM2Yc"
        });
        console.log("📌 FCM 토큰:", token);
        await sendTokenToServer(userId, token);
    }
}

async function sendTokenToServer(userId, token) {
    try {
        await fetch("/api/notifications/register-token", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ userId, token })
        });
    } catch (error) {
        console.error("❌ 서버 요청 중 오류 발생:", error);
    }
}

window.requestNotificationPermission = requestNotificationPermission;
