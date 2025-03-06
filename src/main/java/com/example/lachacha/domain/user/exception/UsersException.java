package com.example.lachacha.domain.user.exception;

import com.example.lachacha.global.exception.MyErrorCode;
import com.example.lachacha.global.exception.MyException;

public class UsersException extends MyException
{
    public UsersException(MyErrorCode errorCode) {
        super(errorCode);
    }
}
