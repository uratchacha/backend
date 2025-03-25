package com.example.lachacha.global.firebase.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FcmTokenRequest {
    private Long userId;
    private String token;
}
