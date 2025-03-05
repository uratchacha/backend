package com.example.lachacha.domain.chats.presentation;

import com.example.lachacha.domain.chats.application.ChatsService;
import com.example.lachacha.domain.chats.dto.request.ChatsRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;


@RestController
@RequestMapping("/chats")
@RequiredArgsConstructor
public class ChatsController
{
    private final ChatsService chatsService;
    @GetMapping("/request")
    public ResponseEntity<Void> requestChat(@RequestParam Long requesterId, @RequestParam Long receiverId)
    {
        chatsService.requestChatRoom(requesterId,receiverId);
        return ResponseEntity.ok().build();
    }


}
