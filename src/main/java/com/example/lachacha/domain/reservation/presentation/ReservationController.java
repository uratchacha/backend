package com.example.lachacha.domain.reservation.presentation;

import com.example.lachacha.domain.reservation.application.ReservationService;
import com.example.lachacha.domain.reservation.dto.ReservationConsentRequestDto;
import com.example.lachacha.domain.reservation.dto.ReservationCreateRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/reservation")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    // 테이블 예약
    @PostMapping("/create")
    public ResponseEntity<Void> createReservation(@RequestBody ReservationCreateRequestDto requestDto) {
        reservationService.createReservation(requestDto.getUserIds(),requestDto.getUserId());
        return ResponseEntity.ok().build();
    }

    // 예약 동의
    @PostMapping("/consent")
    public ResponseEntity<Void> consentToReservation(@RequestBody ReservationConsentRequestDto requestDto) {
        reservationService.consentToReservation(requestDto.getReservationId(),requestDto.getUserId());
        return ResponseEntity.ok().build();
    }
}