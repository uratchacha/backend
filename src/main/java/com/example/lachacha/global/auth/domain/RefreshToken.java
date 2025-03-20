package com.example.lachacha.global.auth.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@AllArgsConstructor
@Getter
@Builder
@NoArgsConstructor(access = PROTECTED)
@Entity
public class RefreshToken
{
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private String refreshToken;

    @Column(nullable = false)
    private Long userId;

}
