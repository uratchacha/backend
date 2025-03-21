package com.example.lachacha.domain.networkingTable.exception;

import com.example.lachacha.global.exception.MyErrorCode;
import com.example.lachacha.global.exception.MyException;

public class NetworkingTableException extends MyException {
    public NetworkingTableException(MyErrorCode errorCode) {
        super(errorCode);
    }
}
