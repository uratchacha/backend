package com.example.lachacha.domain.businessCard.domain;

import com.example.lachacha.domain.user.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BusinessCardRepository extends JpaRepository<BusinessCard, Long> {
    List<BusinessCard> findByUser(Users user);
    Optional<BusinessCard> findByEmailAndUser(String email, Users user);
}