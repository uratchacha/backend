package com.example.lachacha.domain.chats.presentation;

import com.example.lachacha.domain.chats.application.ChatsService;
import com.example.lachacha.domain.chats.dto.ChatsMessageDto;
import com.example.lachacha.domain.chats.dto.request.ExitChatRoomRequestDto;
import com.example.lachacha.domain.chats.dto.request.GroupChatsRequestDto;
import com.example.lachacha.domain.chats.dto.request.JoinGroupRequestDto;
import com.example.lachacha.domain.chats.dto.request.PrivateChatsRequestDto;
import com.example.lachacha.domain.chats.dto.response.ChatRoomResponseDto;
import com.example.lachacha.domain.chats.dto.response.ChatRoomUserResponseDto;
import com.example.lachacha.domain.chats.dto.response.GroupChatRoomResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/chats")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ChatsController
{
    private final ChatsService chatsService;

    @GetMapping("/private-chatroom/request")
    public ResponseEntity<Void> requestChats( @RequestParam("receiverId") Long receiverId)
    {
        chatsService.requestChatRoom(receiverId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/private-chatroom/accept")
    public ResponseEntity<Long> acceptChats(@RequestBody PrivateChatsRequestDto chatsRequestDto)
    {
        return ResponseEntity.status(HttpStatus.OK)
                .body(chatsService.acceptChatRoom(chatsRequestDto));
    }

    @GetMapping("/private-chatroom/reject")
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

    @PostMapping("/exit")
    public ResponseEntity<Void> exitChatRoom(@RequestBody ExitChatRoomRequestDto exitChatRoomRequestDto)
    {
        chatsService.exitChatRoom(exitChatRoomRequestDto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/group-chatroom/create")
    public ResponseEntity<ChatRoomResponseDto> createGroupChat(@RequestBody GroupChatsRequestDto groupChatsRequestDto)
    {
        return ResponseEntity.status(HttpStatus.OK)
                .body(chatsService.createGroupChat(groupChatsRequestDto));
    }

    @PostMapping("/group-chatroom/join")
    public ResponseEntity<ChatRoomResponseDto> joinGroupChat(@RequestBody JoinGroupRequestDto joinGroupRequestDto)
    {
        return ResponseEntity.status(HttpStatus.OK)
                .body(chatsService.joinGroupChat(joinGroupRequestDto));
    }

    @GetMapping("/{chatRoomId}")
    public ResponseEntity<ChatRoomResponseDto> getChatRoomById(@PathVariable Long chatRoomId)
    {
        return ResponseEntity.status(HttpStatus.OK)
                .body(chatsService.findChatRoomById(chatRoomId));
    }
    @GetMapping("/user-nickName/{chatRoomId}")
    public ResponseEntity<ChatRoomUserResponseDto> getChatRoomUserNickNameById(@PathVariable Long chatRoomId)
    {
        return ResponseEntity.status(HttpStatus.OK)
                .body(chatsService.findChatRoomUserById(chatRoomId));
    }



    @GetMapping("/group-chatroom")
    public ResponseEntity<List<GroupChatRoomResponseDto>> getAllGroupChatRoom()
    {
        return ResponseEntity.status(HttpStatus.OK)
                .body(chatsService.findAllGroupChatRoom());
    }

    @GetMapping("/private-chatroom")
    public ResponseEntity<List<ChatRoomResponseDto>> getAllPrivateChatRoom()
    {
        return ResponseEntity.status(HttpStatus.OK)
                .body(chatsService.findAllPrivateChatRoom());

    }
}
