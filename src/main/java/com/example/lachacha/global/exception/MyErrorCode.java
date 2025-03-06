package com.example.lachacha.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum MyErrorCode {
    INVALID_INPUT("INVALID_INPUT", "잘못된 입력값입니다.", HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND("USER_NOT_FOUND", "사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", "서버 내부 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_MESSAGE_TYPE("INVALID_MESSAGE_TYPE","올바르지 않은 메시지 형식입니다.", HttpStatus.BAD_REQUEST),
    NOTIFICATION_ERROR("NOTIFICATION_ERROR","알림 중에 오류가 발생하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String code;       // 예외 코드
    private final String message;    // 예외 메시지
    private final HttpStatus status; // HTTP 상태 코드
}
