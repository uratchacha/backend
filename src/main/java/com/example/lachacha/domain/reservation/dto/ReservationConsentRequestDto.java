package com.example.lachacha.domain.reservation.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReservationConsentRequestDto {
    private Long chatRoomId;
    private Long userId;
}