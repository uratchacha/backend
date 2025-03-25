importScripts(
  'https://www.gstatic.com/firebasejs/9.0.2/firebase-app-compat.js',
);
importScripts(
  'https://www.gstatic.com/firebasejs/9.0.2/firebase-messaging-compat.js',
);

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

// 백그라운드에서 푸시알림 설정
messaging.onBackgroundMessage((payload) => {
  const title = payload.notification.title + ' (onBackgroundMessage)';
  const notificationOptions = {
    body: payload.notification.body,
    icon: '/images/icons/icon-128.png',
  };

  self.registration.showNotification(title, notificationOptions);
});

// 푸시 알림
self.addEventListener('push', function (e) {
  console.log('push: ', e.data.json());
  if (!e.data.json()) return;

  const resultData = e.data.json().notification;
  const notificationTitle = resultData.title;
  const notificationOptions = {
    body: resultData.body,
    icon: resultData.image,
    tag: resultData.tag,
    ...resultData,
  };
  console.log('push: ', { resultData, notificationTitle, notificationOptions });

  self.registration.showNotification(notificationTitle, notificationOptions);
});

// 푸시 알림 클릭했을 때
self.addEventListener('notificationclick', function (event) {
  console.log('notification click');
  const url = '/';
  event.notification.close();
  event.waitUntil(clients.openWindow(url));
});
