package com.example.lachacha.domain.reservation.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ReservationCreateRequestDto {
    private List<Long> userIds;
    private Long userId;
}
