package com.example.lachacha.domain.reservation.domain;

import com.example.lachacha.domain.reservation.enums.ReservationState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    long countByState(ReservationState state);

    List<Reservation> findByState(ReservationState state);

    Optional<Reservation> findById(Long id);

    List<Reservation> findByUserIdsContains(Long userId);
}
