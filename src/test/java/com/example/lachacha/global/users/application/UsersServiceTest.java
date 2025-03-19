package com.example.lachacha.global.users.application;

import com.example.lachacha.domain.user.application.UsersService;
import com.example.lachacha.domain.user.domain.Users;
import com.example.lachacha.domain.user.domain.UsersRepository;
import com.example.lachacha.domain.user.dto.request.UsersLoginRequest;
import com.example.lachacha.domain.user.dto.request.UsersRequestDto;
import com.example.lachacha.domain.user.dto.response.MyPageResponseDto;
import com.example.lachacha.global.auth.application.AuthService;
import com.example.lachacha.global.auth.dto.TokenResponse;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


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

    @BeforeAll
    void setUp()
    {
        usersService = new UsersService(usersRepository,bCryptPasswordEncoder,authService);
    }

    @Test
    @Order(0)
    void 회원가입_테스트()
    {
        UsersRequestDto requestDto = new UsersRequestDto(
                "testuser", "password123", "Hello, I'm a user!",
                "USER", "coding", "learning", "email"
        );

        usersService.create(requestDto);
        // DB에서 직접 조회하여 확인
        Users savedUser = usersRepository.findByUsername("testuser");

        assertNotNull(savedUser);
        assertTrue(bCryptPasswordEncoder.matches("password123", savedUser.getPassword())); // 비밀번호 검증

    }

    @Test
    @Order(1)
    void 로그인_테스트()
    {
        UsersLoginRequest usersLoginRequest = UsersLoginRequest
                .builder()
                .username("testuser")
                .password("password123")
                .build();
        List<Users> users = usersRepository.findAll();
        System.out.println(users.size());

        TokenResponse tokenResponse=usersService.login(usersLoginRequest);

        System.out.println(tokenResponse);

        assertNotNull(tokenResponse);
    }
}
