package com.example.lachacha;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LachachaApplication {

    public static void main(String[] args) {
        SpringApplication.run(LachachaApplication.class, args);
    }

}
