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

    // 예약 동의 (예약이 없으면 생성 후 동의까지)
    @PostMapping("/consent")
    public ResponseEntity<Void> consentToReservation(@RequestBody ReservationConsentRequestDto requestDto) {
        reservationService.consentOrCreateReservation(requestDto.getChatRoomId(), requestDto.getUserId());
        return ResponseEntity.ok().build();
    }


}