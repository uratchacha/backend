package com.example.lachacha.domain.chats.presentation;

import com.example.lachacha.domain.chats.application.ChatsService;
import com.example.lachacha.domain.chats.dto.ChatsMessageDto;
import com.example.lachacha.domain.chats.dto.request.GroupChatsRequestDto;
import com.example.lachacha.domain.chats.dto.request.JoinGroupRequestDto;
import com.example.lachacha.domain.chats.dto.request.PrivateChatsRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/chats")
@RequiredArgsConstructor
public class ChatsController
{
    private final ChatsService chatsService;

    @GetMapping("/private-chatrooms/request")
    public ResponseEntity<Void> requestChats(@RequestParam("requesterId") Long requesterId, @RequestParam("receiverId") Long receiverId)
    {
        chatsService.requestChatRoom(requesterId,receiverId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/private-chatrooms/accept")
    public ResponseEntity<Long> acceptChats(@RequestBody PrivateChatsRequestDto chatsRequestDto)
    {
        return ResponseEntity.status(HttpStatus.OK)
                .body(chatsService.acceptChatRoom(chatsRequestDto));
    }

    @GetMapping("/private-chatrooms/reject")
    public ResponseEntity<Void> rejectChats(@RequestParam("requesterId") Long requesterId)
    {
        chatsService.rejectChatRoom(requesterId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/send")
    public ResponseEntity<Void> sendMessage(@RequestBody ChatsMessageDto chatMessageDto)
    {
        chatsService.sendChatMessage(chatMessageDto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/group-chatrooms/create")
    public ResponseEntity<Long> createGroupChat(@RequestBody GroupChatsRequestDto groupChatsRequestDto)
    {
        return ResponseEntity.status(HttpStatus.OK)
                .body(chatsService.createGroupChat(groupChatsRequestDto));
    }

    @PostMapping("/group-chatrooms/join")
    public ResponseEntity<Long> joinGroupChat(@RequestBody JoinGroupRequestDto joinGroupRequestDto)
    {
        return ResponseEntity.status(HttpStatus.OK)
                .body(chatsService.joinGroupChat(joinGroupRequestDto));
    }
}
