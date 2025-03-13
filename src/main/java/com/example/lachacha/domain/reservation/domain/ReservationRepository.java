package com.example.lachacha.domain.reservation.domain;

import com.example.lachacha.domain.reservation.enums.ReservationState;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    long countByState(ReservationState state);
}
