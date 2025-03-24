package com.example.lachacha.domain.businessCard.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusinessCardRequestDto {
    private String name;
    private String username;
    private String email;
    private String phone;
    private String affiliation;
    private String jobCategory;
    private String jobValue;
}
