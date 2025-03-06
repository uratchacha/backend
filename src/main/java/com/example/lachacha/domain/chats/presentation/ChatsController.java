package com.example.lachacha.domain.chats.presentation;

import com.example.lachacha.domain.chats.application.ChatsService;
import com.example.lachacha.domain.chats.domain.ChatRoom;
import com.example.lachacha.domain.chats.dto.request.PrivateChatsRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/chats")
@RequiredArgsConstructor
public class ChatsController
{
    private final ChatsService chatsService;

    @GetMapping("/request")
    public ResponseEntity<Void> requestChats(@RequestParam("requesterId") Long requesterId, @RequestParam("receiverId") Long receiverId)
    {
        chatsService.requestChatRoom(requesterId,receiverId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/accept")
    public ResponseEntity<Void> acceptChats(@RequestBody PrivateChatsRequestDto chatsRequestDto)
    {
        chatsService.acceptChatRoom(chatsRequestDto);
        return ResponseEntity.ok().build();
    }

}
