package com.example.lachacha.global.firebase.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FcmNotificationRequest {
    private String title;
    private String body;
}
