package com.example.lachacha.global.exception;

import lombok.Getter;


@Getter
public class MyException extends RuntimeException {
    private final MyErrorCode errorCode;

    public MyException(MyErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
