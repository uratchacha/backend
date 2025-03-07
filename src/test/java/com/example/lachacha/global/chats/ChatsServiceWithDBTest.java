package com.example.lachacha.global.chats;

import com.example.lachacha.domain.chats.application.ChatsService;
import com.example.lachacha.domain.chats.domain.ChatRoomRepository;
import com.example.lachacha.domain.chats.domain.PrivateChatRoom;
import com.example.lachacha.domain.chats.dto.ChatsMessageDto;
import com.example.lachacha.domain.chats.dto.request.PrivateChatsRequestDto;
import com.example.lachacha.domain.user.application.UsersService;
import com.example.lachacha.domain.user.domain.Users;
import com.example.lachacha.domain.user.domain.UsersRepository;
import com.example.lachacha.global.webSocket.chats.ChatHandler;
import com.example.lachacha.global.webSocket.notifications.NotificationHandler;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ChatsServiceWithDBTest
{
    @Mock
    private NotificationHandler notificationHandler;
    @Mock
    private ChatHandler chatHandler;
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
        chatsService = new ChatsService(notificationHandler,chatHandler,chatRoomRepository,usersService);
        user1 = Users.builder().username("User1").password("54545").build();
        user2 = Users.builder().username("User2").password("1335").build();

        usersRepository.save(user1);
        usersRepository.save(user2);

    }

    @Test
    @Order(0)
    void acceptChatRoomTest() throws IOException {
        // 채팅방 요청 DTO 생성
        //setUser();
        List<Users> users = usersRepository.findAll();
        System.out.println(users.get(1).getId());
        PrivateChatsRequestDto chatsRequestDto = new PrivateChatsRequestDto(1L, 2L);


        // 채팅방 생성 메서드 호출
        chatsService.acceptChatRoom(chatsRequestDto);
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(notificationHandler).sendNotification(Mockito.eq(1L), messageCaptor.capture());

        System.out.println(messageCaptor.getValue());

        // 채팅방이 DB에 잘 저장되었는지 확인
        PrivateChatRoom privateChatRoom = (PrivateChatRoom) chatRoomRepository.findAll().get(0);
        System.out.println(privateChatRoom.getMaxSize());

        for(Users user : privateChatRoom.getMembers()) {
            System.out.println(user);
        }

        assertThat(privateChatRoom).isNotNull();
        assertThat(privateChatRoom.getMembers().get(0).getId()).isEqualTo(1L);
        assertThat(privateChatRoom.getMembers().get(1).getId()).isEqualTo(2L);
    }

    @Test
    @Order(1)
    void sendPrivateChatRequestTest() throws Exception {
        ChatsMessageDto chatMessageDto = new ChatsMessageDto(
                LocalDateTime.now(),
                1L,
                "안녕하세요! 채팅 테스트 메시지입니다.",
                user1.getUsername()
        );

        chatsService.sendChatMessage(chatMessageDto);

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(chatHandler).handleTextMessage(Mockito.eq(chatMessageDto.chatRoomId()), messageCaptor.capture());

        System.out.println("전송된 메시지: " + messageCaptor.getValue());
        assertThat(messageCaptor.getValue()).contains("안녕하세요! 채팅 테스트 메시지입니다.");
    }

    @Test
    void rejectChatRoomTest() throws Exception {

        chatsService.rejectChatRoom(1L);
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(notificationHandler).sendNotification(Mockito.eq(1L), messageCaptor.capture());

        System.out.println(messageCaptor.getValue());

        assertThat(messageCaptor).isNotNull();
    }
}
