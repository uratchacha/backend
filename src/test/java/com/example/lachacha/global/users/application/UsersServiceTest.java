package com.example.lachacha.global.users.application;

import com.example.lachacha.domain.user.application.UsersService;
import com.example.lachacha.domain.user.domain.Users;
import com.example.lachacha.domain.user.domain.UsersRepository;
import com.example.lachacha.domain.user.dto.request.UserUpdateRequestDto;
import com.example.lachacha.domain.user.dto.request.UsersLoginRequest;
import com.example.lachacha.domain.user.dto.request.UsersRequestDto;
import com.example.lachacha.domain.user.dto.response.MyPageResponseDto;
import com.example.lachacha.domain.user.dto.response.UserProfileDto;
import com.example.lachacha.global.auth.application.AuthService;
import com.example.lachacha.global.auth.dto.TokenResponse;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


@SpringBootTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UsersServiceTest
{

    private UsersService usersService;
    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private AuthService authService;

    @Mock
    private AuthService authServiceMock;

    Users savedUser;
    @BeforeAll
    void setUp()
    {

        usersService = new UsersService(usersRepository,bCryptPasswordEncoder,authService);
        UsersRequestDto dto = UsersRequestDto.builder()
                .username("user123222")
                .password("securePassword")
                .affiliation("Company ABC")
                .career("5 years")
                .contactInfo("010-1234-5678")
                .email("usersdsd@example.com")
                .interestJobCategory("Developer")
                .interestJobValue("Java")
                .jobCategory("IT")
                .jobValue("Engineering")
                .interests((List.of("value1", "value2")))
                .participationPurpose("Learning")
                .build();
        usersService.create(dto);
    }

    @Test
    @Order(0)
    void 회원가입_테스트()
    {
        UsersRequestDto dto = UsersRequestDto.builder()
                .username("user123")
                .password("securePassword")
                .affiliation("Company ABC")
                .career("5 years")
                .contactInfo("010-1234-5678")
                .email("user@example.com")
                .interestJobCategory("Developer")
                .interestJobValue("Java")
                .jobCategory("IT")
                .jobValue("Engineering")
                .interests((List.of("value1", "value2")))
                .participationPurpose("Learning")
                .build();
        System.out.println( usersRepository.countByJobCategory("IT"));
        usersService.create(dto);
        // DB에서 직접 조회하여 확인
        savedUser = usersRepository.findByUsername("user123");
        when(authServiceMock.findUsersByAuth()).thenReturn(savedUser);

        System.out.println(savedUser.getNickName());
        assertNotNull(savedUser);
        assertTrue(bCryptPasswordEncoder.matches("securePassword", savedUser.getPassword())); // 비밀번호 검증

    }

    @Test
    @Order(1)
    void 로그인_테스트()
    {
        UsersLoginRequest usersLoginRequest = UsersLoginRequest
                .builder()
                .username("user123")
                .password("securePassword")
                .build();
        List<Users> users = usersRepository.findAll();
        System.out.println(users.size());

        TokenResponse tokenResponse=usersService.login(usersLoginRequest);

        System.out.println(tokenResponse);

        assertNotNull(tokenResponse);
    }
    @Test
    @Order(2)
    void 유저조희_테스트()
    {
        List<UserProfileDto> usersRequestDtoList =usersService.getAllUsers();
        List<Users> users = usersRepository.findAll();
        for(Users user : users)
            System.out.println(user.isParticipate());
        System.out.println(usersRequestDtoList.size());
        System.out.println(users.size());
    }

    @Test
    @Order(3)
    void 유저업데이트_테스트()
    {
        usersService = new UsersService(usersRepository,bCryptPasswordEncoder,authServiceMock);
        UserUpdateRequestDto requestDto = UserUpdateRequestDto.builder()
                .name("kimaaa")
                .build();
        when(authServiceMock.findUsersByAuth()).thenReturn(savedUser);
        usersService.updateUser(requestDto);
    }

}
