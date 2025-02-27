package com.example.lachacha.global.exception;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SampleController {

    @GetMapping("/test")
    public String test(@RequestParam(required = false) String name) {
        if (name == null || name.isBlank()) {
            throw new MyException(MyErrorCode.INVALID_INPUT); // 400 BAD_REQUEST 발생
        }

        if ("admin".equals(name)) {
            throw new MyException(MyErrorCode.USER_NOT_FOUND); // 404 NOT_FOUND 발생
        }

        return "Hello, " + name;
    }
}