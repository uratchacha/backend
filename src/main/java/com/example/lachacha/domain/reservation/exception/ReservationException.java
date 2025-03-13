package com.example.lachacha.domain.reservation.exception;

import com.example.lachacha.global.exception.MyErrorCode;
import com.example.lachacha.global.exception.MyException;

public class ReservationException extends MyException {
    public ReservationException(MyErrorCode errorCode) {
        super(errorCode);
    }
}
