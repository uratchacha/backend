package com.example.lachacha.global.exception;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SampleController {

    @GetMapping("/test")
    public String test(@RequestParam(required = false) String name) {
        if (name == null || name.isBlank()) {
            throw new MyException(MyErrorCode.INVALID_INPUT);
        }

        if ("admin".equals(name)) { // 특정 사용자 찾을 수 없는 경우 예외 발생
            throw new MyException(MyErrorCode.USER_NOT_FOUND);
        }

        return "Hello, " + name;
    }
}
