package com.example.lachacha.domain.networkingTable.enums;

public enum TableState {
    AVAILABLE,  // 사용 가능 (예약 및 배정 가능)
    RESERVED,   // 예약됨 (QR 스캔 전)
    OCCUPIED    // 사용 중 (QR 스캔 완료, 네트워킹 진행 중)
}