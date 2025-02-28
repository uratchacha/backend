package com.example.lachacha.global.auth.application;

import com.example.lachacha.domain.user.domain.Users;
import com.example.lachacha.global.auth.jwt.JwtProperties;
import com.example.lachacha.global.auth.jwt.TokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.context.annotation.Import;


import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

@Import({TokenProvider.class,JwtProperties.class})
@DataRedisTest
public class TokenProviderTest {



    @Autowired
    private TokenProvider tokenProvider;

    private final Duration EXPIRATION = Duration.ofHours(1);
    private final Users user = Users.builder().id(1L).username("admin").password("admin").build();
    private String token ;
    @BeforeEach
    void setUp()
    {
        token = tokenProvider.generateToken(EXPIRATION,user);
    }

    @Test
    void generateToken()
    {
        System.out.println(token);
        assertNotNull(token);
    }

    @Test
    void validateToken()
    {
        boolean valid = tokenProvider.validateToken(token);
        assertTrue(valid);
    }

    @Test
    void validateToken_잘못된_토큰_테스트() {
        // Given
        token = "invalid.token.value";

        // When
        boolean isValid = tokenProvider.validateToken(token);

        // Then
        assertFalse(isValid);
    }

    @Test
    void getUserId_토큰에서_유저ID_추출_테스트() {

        // When
        Long userId = tokenProvider.getUserId(token);

        // Then
        assertEquals(1L, userId);
    }
}
