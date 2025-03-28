package com.example.lachacha.domain.chats.dto.response;

import com.example.lachacha.domain.user.domain.Users;
import lombok.Builder;
import java.util.List;
import java.util.stream.Collectors;

@Builder
public record ChatRoomUserResponseDto(
        List<String> userNickNames
) {
    public static ChatRoomUserResponseDto from(List<Users> users) {
        List<String> nickNames = users.stream()
                .map(Users::getNickName) // Users 객체에서 닉네임 가져오기
                .collect(Collectors.toList());

        // 빌더 패턴을 사용하여 ChatRoomUserResponseDto 생성
        return ChatRoomUserResponseDto.builder()
                .userNickNames(nickNames)
                .build();
    }

}
