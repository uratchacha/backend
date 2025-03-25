package com.example.lachacha.domain.businessCard.exception;

import com.example.lachacha.global.exception.MyErrorCode;
import com.example.lachacha.global.exception.MyException;

public class BusinessCardException extends MyException
{
    public BusinessCardException(MyErrorCode errorCode) {
        super(errorCode);
    }
}
