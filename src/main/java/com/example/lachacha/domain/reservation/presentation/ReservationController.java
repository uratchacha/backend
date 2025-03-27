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

    //대기시간 확인
    @GetMapping("/wait-time/{chatRoomId}")
    public ResponseEntity<Integer> getWaitingTime(@PathVariable("chatRoomId") Long chatRoomId) {
        // 현재는 하드코딩된 0 반환
        int waitingTime = 0;

        // 로그 찍거나 chatRoomId 활용할 수 있음
        System.out.println("요청 받은 채팅방 ID: " + chatRoomId);

        return ResponseEntity.ok(waitingTime);
    }

}