package com.example.lachacha.domain.businessCard.dto;

import com.example.lachacha.domain.businessCard.domain.BusinessCard;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusinessCardResponseDto {
    private Long id;
    private String name;
    private String username;
    private String email;
    private String phone;
    private String affiliation;
    private String jobCategory;
    private String jobValue;

    public static BusinessCardResponseDto fromEntity(BusinessCard card) {
        return BusinessCardResponseDto.builder()
                .id(card.getId())
                .name(card.getName())
                .username(card.getUsername())
                .email(card.getEmail())
                .phone(card.getPhone())
                .affiliation(card.getAffiliation())
                .jobCategory(card.getJobCategory())
                .jobValue(card.getJobValue())
                .build();
    }
}
