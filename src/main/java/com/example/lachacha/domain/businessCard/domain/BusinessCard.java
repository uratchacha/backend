package com.example.lachacha.domain.businessCard.domain;

import com.example.lachacha.domain.user.domain.Users;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusinessCard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String username;
    private String email;
    private String contactInfo;
    private String affiliation;
    private String jobCategory;
    private String jobValue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users user;
}
