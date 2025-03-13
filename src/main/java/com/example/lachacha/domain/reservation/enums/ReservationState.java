package com.example.lachacha.domain.reservation.enums;

public enum ReservationState {
    REQUESTED,  // 예약 요청 (일부 유저만 동의, 아직 대기열에 추가되지 않음)
    PENDING,    // 배정 대기 (예약은 했지만 테이블이 배정되지 않음)
    CONFIRMED   // 배정 완료 (테이블이 배정됨)
}