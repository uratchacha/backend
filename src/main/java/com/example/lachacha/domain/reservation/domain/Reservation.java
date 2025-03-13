package com.example.lachacha.domain.reservation.domain;

import com.example.lachacha.domain.reservation.enums.ReservationState;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "table_reservation")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 예약 ID

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ReservationState state;

    @Column
    private LocalDateTime reservedAt; // 예약 시간

    @ElementCollection
    @CollectionTable(name = "table_reservation_users", joinColumns = @JoinColumn(name = "reservation_id"))
    @Column(name = "user_id")
    private List<Long> userIds = new ArrayList<>(); // 예약한 사용자 ID 목록

    @ElementCollection
    @CollectionTable(name = "table_consented_users", joinColumns = @JoinColumn(name = "reservation_id"))
    @Column(name = "user_id")
    private List<Long> consentedUserIds = new ArrayList<>(); // 예약을 동의한 사용자 ID 목록

    public void consent(Long userId){
        consentedUserIds.add(userId);
    }

    public void updateState(ReservationState state){
        this.state= state;
    }

    public void updateTime(LocalDateTime time){
        this.reservedAt = time;
    }

}