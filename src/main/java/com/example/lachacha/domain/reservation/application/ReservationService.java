package com.example.lachacha.domain.reservation.application;


import com.example.lachacha.domain.networkingTable.domain.NetworkingTable;
import com.example.lachacha.domain.networkingTable.domain.NetworkingTableRepository;
import com.example.lachacha.domain.networkingTable.enums.TableState;
import com.example.lachacha.domain.reservation.domain.Reservation;
import com.example.lachacha.domain.reservation.domain.ReservationRepository;
import com.example.lachacha.domain.reservation.enums.ReservationState;
import com.example.lachacha.domain.reservation.exception.ReservationException;
import com.example.lachacha.global.exception.MyErrorCode;
import com.example.lachacha.global.webSocket.networkingTables.TableWaitTimeHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final TableWaitTimeHandler waitTimeHandler;


    private static final int NETWORKING_DURATION_MINUTES = 30;
    private final NetworkingTableRepository networkingTableRepository;
    private final TableWaitTimeHandler waitTimeHandler;

    // ✅ 대기시간 리스트 생성 메서드
    public List<Integer> calculateEstimatedWaitTimeList() {
        // 사용 가능한 테이블이 하나라도 있으면 대기시간 0으로 처리
        boolean hasAvailableTable = networkingTableRepository.existsByState(TableState.AVAILABLE);
        if (hasAvailableTable) {
            return List.of();
        }

        // 예약 또는 진행 중인 테이블 조회
        List<NetworkingTable> activeTables = networkingTableRepository.findByStateIn(List.of(TableState.RESERVED, TableState.OCCUPIED));

        // 네트워킹 시작시간을 기준으로 정렬 (시작시간이 null이면 가장 마지막)
        return activeTables.stream()
                .sorted((t1, t2) -> {
                    if (t1.getStartTime() == null && t2.getStartTime() == null) return 0;
                    if (t1.getStartTime() == null) return 1;
                    if (t2.getStartTime() == null) return -1;
                    return t1.getStartTime().compareTo(t2.getStartTime());
                })
                .map(table -> {
                    if (table.getStartTime() == null) {
                        return NETWORKING_DURATION_MINUTES; // 시작시간이 없는 경우 가장 마지막 (최대 예상시간)
                    }
                    return NETWORKING_DURATION_MINUTES - (int) Duration.between(table.getStartTime(), LocalDateTime.now()).toMinutes();
                })
                .collect(Collectors.toList());
    }

    //웹소켓을 통해 예상 대기시간 업데이트
    public void updateWaitTime() {
        List<Integer> estimatedWaitTimes = calculateEstimatedWaitTimeList();
        waitTimeHandler.sendWaitTimeUpdate(estimatedWaitTimes);
    }






    // 테이블 예약 생성
    @Transactional
    public void createReservation(List<Long> userIds, Long userId) {
        Reservation reservation = Reservation.builder()
                .userIds(userIds)
                .state(ReservationState.REQUESTED)
                .build();


        Reservation savedReservation = reservationRepository.save(reservation);
        consentToReservation(savedReservation.getId(),userId);

    }

    // 예약 동의
    @Transactional
    public void consentToReservation(Long reservationId, Long userId) {
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

    //예상대기시간 계산
    private int calculateEstimatedWaitTime() {
        long pendingReservations = reservationRepository.countByState(ReservationState.PENDING);
        int estimatedTime = (int) (pendingReservations * 5); // 한 테이블당 5분 예상 (예시)
        return estimatedTime;
    }

    //예상 대기시간을 WebSocket으로 전송
    public void updateWaitTime() {
        int estimatedWaitTime = calculateEstimatedWaitTime();
        waitTimeHandler.sendWaitTimeUpdate(estimatedWaitTime);
    }


    //예약 사용자 검증
    private boolean isUserNotInReservation(Reservation reservation, Long userId) {
        return !reservation.getUserIds().contains(userId);
    }

    //중복 예약 동의 검증
    private boolean hasUserAlreadyConsented(Reservation reservation, Long userId) {
        return reservation.getConsentedUserIds().contains(userId);
    }

    // 모든 사용자가 동의했는지 확인 후 예약 상태 변경
    private void checkAndConfirmReservation(Reservation reservation) {
        if (reservation.getConsentedUserIds().containsAll(reservation.getUserIds())) {
            reservation.updateState(ReservationState.PENDING);
            reservation.updateTime(LocalDateTime.now());
        }
        reservationRepository.save(reservation);
    }
}