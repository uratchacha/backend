package com.example.lachacha.domain.reservation.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReservationCreateRequestDto {
    private Long chatRoomId;
    private Long userId;
}
