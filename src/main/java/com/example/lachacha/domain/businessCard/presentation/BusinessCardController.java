package com.example.lachacha.domain.businessCard.presentation;

import com.example.lachacha.domain.businessCard.application.BusinessCardService;
import com.example.lachacha.domain.businessCard.dto.BusinessCardRequestDto;
import com.example.lachacha.domain.businessCard.dto.BusinessCardResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/business-cards")
@RequiredArgsConstructor
public class BusinessCardController {

    private final BusinessCardService businessCardService;

    // 명함 리스트 조회
    @GetMapping
    public ResponseEntity<List<BusinessCardResponseDto>> getMyCards() {
        return ResponseEntity.ok(businessCardService.getMyBusinessCards());
    }

    // 명함 추가
    @PostMapping
    public ResponseEntity<BusinessCardResponseDto> addCard(@RequestBody BusinessCardRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(businessCardService.addBusinessCard(dto));
    }

    // 명함 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCard(@PathVariable Long id) {
        businessCardService.deleteBusinessCard(id);
        return ResponseEntity.noContent().build();
    }
}