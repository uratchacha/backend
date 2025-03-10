package com.example.lachacha.global.chats;

import com.example.lachacha.domain.chats.application.ChatsService;
import com.example.lachacha.domain.chats.domain.ChatRoom;
import com.example.lachacha.domain.chats.domain.ChatRoomRepository;
import com.example.lachacha.domain.chats.domain.PrivateChatRoom;
import com.example.lachacha.domain.chats.dto.ChatsMessageDto;
import com.example.lachacha.domain.chats.dto.request.GroupChatsRequestDto;
import com.example.lachacha.domain.chats.dto.request.JoinGroupRequestDto;
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
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
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
    private Users user3;
    private Users user4;

    private UsersService usersService;

    @BeforeAll
    void setUp() {


        usersService = new UsersService(usersRepository);
        chatsService = new ChatsService(notificationHandler,chatHandler,chatRoomRepository,usersService);
        user1 = Users.builder().username("User1").password("54545").build();
        user2 = Users.builder().username("User2").password("1335").build();
        user3 = Users.builder().username("User3").password("1335d").build();
        user4 = Users.builder().username("User4").password("1335a").build();
        usersRepository.save(user1);
        usersRepository.save(user2);
        usersRepository.save(user3);
        usersRepository.save(user4);
    }

    @BeforeEach
    void init()
    {
        Mockito.reset(notificationHandler, chatHandler);
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
    @Order(2)
    void creatAndjJoinGroupChatTest() throws Exception
    {
        GroupChatsRequestDto groupChatsRequestDto = GroupChatsRequestDto.builder()
                .maxSize(3)
                .userId(1L)
                .build();

        ChatRoom chatRoom =chatRoomRepository.findById(chatsService.createGroupChat(groupChatsRequestDto)).orElse(null);
        JoinGroupRequestDto joinGroupRequestDto = JoinGroupRequestDto.builder()
                .chatRoomId(chatRoom.getId())
                .userId(2L)
                .build();
        JoinGroupRequestDto joinGroupRequestDto2 = JoinGroupRequestDto.builder()
                .chatRoomId(chatRoom.getId())
                .userId(3L)
                .build();

        // 그룹 채팅에 사용자 추가
        chatsService.joinGroupChat(joinGroupRequestDto);
        chatsService.joinGroupChat(joinGroupRequestDto2);

        // 알림 메시지 캡처를 위한 ArgumentCaptor 설정
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);

        // 알림 보내기 검증
        Mockito.verify(notificationHandler).sendNotification(Mockito.eq(3L), messageCaptor.capture());
        Mockito.verify(notificationHandler).sendNotification(Mockito.eq(2L), messageCaptor.capture());
        Mockito.verify(notificationHandler).sendNotification(Mockito.eq(1L), messageCaptor.capture());

        // 메시지 출력 (디버깅용)
        System.out.println(messageCaptor.getAllValues());

        // Assertions (검증)
        assertThat(chatRoom).isNotNull(); // 채팅방이 null이 아니어야 한다.
        assertThat(chatRoom.getMaxSize()).isEqualTo(3); // 최대 크기 확인
        assertThat(chatRoom.getMembers().size()).isEqualTo(3); // 채팅방 멤버 수 확인

        // 각 사용자에게 전송된 메시지 내용 검증
    }

    @Test
    @Order(3)

    void rejectChatRoomTest() throws Exception {

        chatsService.rejectChatRoom(1L);
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(notificationHandler).sendNotification(Mockito.eq(1L), messageCaptor.capture());

        System.out.println(messageCaptor.getValue());


        Mockito.verify(notificationHandler, Mockito.times(1))
                .sendNotification(Mockito.eq(1L), messageCaptor.capture());
        assertThat(messageCaptor).isNotNull();
    }

    @Test
    @Order(4)
    void sendMessageGroupChatRoom() throws Exception
    {
        ChatsMessageDto chatMessageDto = new ChatsMessageDto(
                LocalDateTime.now(),
                2L,
                "안녕하세요! 채팅 테스트 메시지입니다.",
                user1.getUsername()
        );
        chatsService.sendChatMessage(chatMessageDto);
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);

        // 알림 보내기 검증
        Mockito.verify(chatHandler).handleTextMessage(Mockito.eq(chatMessageDto.chatRoomId()), messageCaptor.capture());
        System.out.println(messageCaptor.getValue());
    }

}
