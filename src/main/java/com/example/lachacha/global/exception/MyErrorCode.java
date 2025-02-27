package com.example.lachacha.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MyErrorCode {
    INVALID_INPUT("INVALID_INPUT","잘못된 입력값입니다."),
    USER_NOT_FOUND("USER_NOT_FOUND", "사용자를 찾을 수 없습니다."),
    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", "서버 내부 오류가 발생했습니다.");

    private final String code;
    private final String message;
}
