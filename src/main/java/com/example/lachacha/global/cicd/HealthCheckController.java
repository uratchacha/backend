package com.example.lachacha.global.cicd;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class HealthCheckController {

    @Value("${server.env}")
    private String env;

    @GetMapping("/env")
    public ResponseEntity<?> getEnv() {
        Map<String, String> responseData = new HashMap<>();
        return ResponseEntity.ok(env);
    }
}
