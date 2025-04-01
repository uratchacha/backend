package com.example.lachacha.domain.businessCard.application;

import com.example.lachacha.domain.businessCard.domain.BusinessCard;
import com.example.lachacha.domain.businessCard.domain.BusinessCardRepository;
import com.example.lachacha.domain.businessCard.dto.BusinessCardRequestDto;
import com.example.lachacha.domain.businessCard.dto.BusinessCardResponseDto;
import com.example.lachacha.domain.businessCard.exception.BusinessCardException;
import com.example.lachacha.domain.user.domain.Users;
import com.example.lachacha.global.auth.application.AuthService;
import com.example.lachacha.global.exception.MyErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.lachacha.global.exception.MyErrorCode.DUPLICATE_BUSINESS_CARD;

@Service
@RequiredArgsConstructor
public class BusinessCardService {

    private final BusinessCardRepository businessCardRepository;
    private final AuthService authService;

    public List<BusinessCardResponseDto> getMyBusinessCards() {
        Users currentUser = authService.findUsersByAuth();
        return businessCardRepository.findByUser(currentUser).stream()
                .map(BusinessCardResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    public BusinessCardResponseDto addBusinessCard(BusinessCardRequestDto dto) {
        Users currentUser = authService.findUsersByAuth();

        Optional<BusinessCard> existingCard = businessCardRepository.findByUsernameAndUser(dto.getUsername(), currentUser);
        if (existingCard.isPresent()) {
            throw new BusinessCardException(DUPLICATE_BUSINESS_CARD);
        }

        BusinessCard card = BusinessCard.builder()
                .name(dto.getName())
                .username(dto.getUsername())
                .email(dto.getEmail())
                .contactInfo(dto.getContactInfo())
                .affiliation(dto.getAffiliation())
                .jobCategory(dto.getJobCategory())
                .jobValue(dto.getJobValue())
                .user(currentUser)
                .build();
        businessCardRepository.save(card);
        return BusinessCardResponseDto.fromEntity(card);
    }

    public void deleteBusinessCard(Long id) {
        Users currentUser = authService.findUsersByAuth();
        BusinessCard card = businessCardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 명함입니다."));
        if (!card.getUser().getId().equals(currentUser.getId())) {
            throw new SecurityException("삭제 권한이 없습니다.");
        }
        businessCardRepository.delete(card);
    }

    public BusinessCardResponseDto updateBusinessCard(BusinessCardRequestDto dto) {
        Users currentUser = authService.findUsersByAuth();

        String username = currentUser.getUsername();
        BusinessCard card = businessCardRepository.findByUsernameAndUser(username, currentUser)
                .orElseThrow(() -> new BusinessCardException(MyErrorCode.INVALID_INPUT));

        if (!card.getUser().getId().equals(currentUser.getId())) {
            throw new BusinessCardException(MyErrorCode.USER_NOT_FOUND);
        }


        Optional<BusinessCard> duplicate = businessCardRepository.findByUsernameAndUser(dto.getUsername(), currentUser);
        if (duplicate.isPresent() && !duplicate.get().getId().equals(card.getId())) {
            throw new BusinessCardException(MyErrorCode.DUPLICATE_BUSINESS_CARD);
        }

        // 값 업데이트
        card.setName(dto.getName());
        card.setUsername(dto.getUsername());
        card.setEmail(dto.getEmail());
        card.setContactInfo(dto.getContactInfo());
        card.setAffiliation(dto.getAffiliation());
        card.setJobCategory(dto.getJobCategory());
        card.setJobValue(dto.getJobValue());

        return BusinessCardResponseDto.fromEntity(businessCardRepository.save(card));
    }
}