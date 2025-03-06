package com.example.lachacha.global.chats;

import com.example.lachacha.domain.chats.application.ChatsService;
import com.example.lachacha.domain.chats.domain.ChatRoomRepository;
import com.example.lachacha.domain.chats.domain.PrivateChatRoom;
import com.example.lachacha.domain.chats.dto.request.PrivateChatsRequestDto;
import com.example.lachacha.domain.user.application.UsersService;
import com.example.lachacha.domain.user.domain.Users;
import com.example.lachacha.domain.user.domain.UsersRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ChatsServiceWithDBTest
{
    @Autowired
    private ChatRoomRepository chatRoomRepository;
    @Autowired
    private UsersRepository usersRepository;

    private ChatsService chatsService;
    private Users user1;
    private Users user2;

    private UsersService usersService;

    @BeforeEach
    void setUp() {


        usersService = new UsersService(usersRepository);
        chatsService = new ChatsService(null,chatRoomRepository,usersService);


    }
    @Transactional
    void setUser()
    {
        user1 = Users.builder().username("User1").password("54545").build();
        user2 = Users.builder().username("User2").password("1335").build();

        usersRepository.save(user1);
        usersRepository.save(user2);
    }
    @Test
    void acceptChatRoomTest() {
        // 채팅방 요청 DTO 생성
        setUser();
        List<Users> users = usersRepository.findAll();
        System.out.println(users.get(1).getId());
        PrivateChatsRequestDto chatsRequestDto = new PrivateChatsRequestDto(1L, 2L);


        // 채팅방 생성 메서드 호출
        chatsService.acceptChatRoom(chatsRequestDto);

        // 채팅방이 DB에 잘 저장되었는지 확인
        PrivateChatRoom privateChatRoom = (PrivateChatRoom) chatRoomRepository.findAll().get(0);
        assertThat(privateChatRoom).isNotNull();
        assertThat(privateChatRoom.getUser1().getId()).isEqualTo(1L);
        assertThat(privateChatRoom.getUser2().getId()).isEqualTo(2L);
    }

}
