package com.example.lachacha.domain.reservation.application;

import com.example.lachacha.domain.chats.application.ChatsService;
import com.example.lachacha.domain.chats.dto.response.ChatRoomResponseDto;
import com.example.lachacha.domain.reservation.domain.Reservation;
import com.example.lachacha.domain.reservation.domain.ReservationRepository;
import com.example.lachacha.domain.reservation.enums.ReservationState;
import com.example.lachacha.domain.reservation.exception.ReservationException;
import com.example.lachacha.domain.user.domain.Users;
import com.example.lachacha.global.exception.MyErrorCode;
import com.example.lachacha.global.webSocket.networkingTables.TableWaitTimeHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;


@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final TableWaitTimeHandler tableWaitTimeHandler;
    private final ChatsService chatsService;

    // 테이블 예약 생성
    @Transactional
    public void createReservation(Long chatRoomId, Long userId) {
        ChatRoomResponseDto chatRoom = chatsService.findChatRoomById(chatRoomId);

        List<Long> userIds = chatRoom.members().stream()
                .map(Users::getId)
                .toList();

        Reservation reservation = Reservation.builder()
                .userIds(new ArrayList<>(userIds))
                .consentedUserIds(new ArrayList<>(List.of(userId)))
                .state(ReservationState.NOT_RESERVED)
                .build();

        Reservation savedReservation = reservationRepository.save(reservation);

        tableWaitTimeHandler.addNotReservedReservation(chatRoomId, savedReservation.getId());

    }

    // 예약 동의
    @Transactional
    public void consentToReservation(Long chatRoomId, Long userId) {
        Long reservationId = tableWaitTimeHandler.getReservationIdByChatRoomId(chatRoomId);

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationException(MyErrorCode.RESERVATION_NOT_FOUND));

        // 예약 사용자 검증
        if (isUserNotInReservation(reservation, userId)) {
            throw new ReservationException(MyErrorCode.USER_NOT_IN_RESERVATION);
        }

        // 예약 중복 동의 검증
        if (hasUserAlreadyConsented(reservation, userId)) {
            throw new ReservationException(MyErrorCode.USER_ALREADY_CONSENTED);
        }

        reservation.consent(userId);

        // 모든 사용자가 동의했는지 확인 후 상태 변경
        checkAndConfirmReservation(reservation);

        reservationRepository.save(reservation);
    }


    // 모든 사용자가 동의했는지 확인 후 예약 상태 변경
    private void checkAndConfirmReservation(Reservation reservation) {
        if (reservation.getConsentedUserIds().containsAll(reservation.getUserIds())) {
            reservation.updateState(ReservationState.RESERVED);
            reservation.updateTime(LocalDateTime.now());

            Long chatRoomId = tableWaitTimeHandler.findChatRoomIdByUsers(reservation.getUserIds());
            tableWaitTimeHandler.confirmReservation(chatRoomId, reservation.getId());
            reservationRepository.save(reservation);
        }
    }

    //예약 사용자 검증
    private boolean isUserNotInReservation(Reservation reservation, Long userId) {
        return !reservation.getUserIds().contains(userId);
    }

    //중복 예약 동의 검증
    private boolean hasUserAlreadyConsented(Reservation reservation, Long userId) {
        return reservation.getConsentedUserIds().contains(userId);
    }

    //예약상태 변경(배정됨)
    @Transactional
    public void assignReservationToTable(Long chatRoomId) {
        Long reservationId = tableWaitTimeHandler.getReservationIdByChatRoomId(chatRoomId);

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationException(MyErrorCode.RESERVATION_NOT_FOUND));

        // ✅ 상태를 COMPLETED(배정 완료)로 변경
        reservation.updateState(ReservationState.COMPLETED);

        reservationRepository.save(reservation);

    }

}