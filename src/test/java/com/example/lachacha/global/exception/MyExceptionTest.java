package com.example.lachacha.global.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MyExceptionTest {

    @Test
    void testMyException() {
        // Given
        MyErrorCode errorCode = MyErrorCode.INVALID_INPUT;

        // When
        MyException exception = new MyException(errorCode);

        // Then
        assertEquals(errorCode, exception.getErrorCode());
        assertEquals(errorCode.getMessage(), exception.getMessage());
    }
}
