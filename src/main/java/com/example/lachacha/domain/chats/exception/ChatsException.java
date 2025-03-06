package com.example.lachacha.domain.chats.exception;

import com.example.lachacha.global.exception.MyErrorCode;
import com.example.lachacha.global.exception.MyException;

public class ChatsException extends MyException
{
    public ChatsException(MyErrorCode errorCode) {
        super(errorCode);
    }
}
