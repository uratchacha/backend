package com.example.lachacha.global.auth.filter;

import com.example.lachacha.domain.user.domain.Users;
import com.example.lachacha.global.auth.application.TokenProviderTest;
import com.example.lachacha.global.auth.jwt.TokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

@Import(TokenProviderTest.class)
@DataRedisTest
public class TokenAuthenticationFilterTest
{
    @Autowired
    private TokenProvider tokenProvider;
    private final Duration EXPIRATION = Duration.ofHours(1);
    private final Users user = Users.builder().id(1L).username("admin").password("admin").build();
    private String token ;

    private TokenAuthenticationFilter tokenAuthenticationFilter;


    @BeforeEach
    void setUp()
    {
        token = tokenProvider.generateToken(EXPIRATION,user);
        System.out.println(token);
        tokenAuthenticationFilter = new TokenAuthenticationFilter(tokenProvider);
    }


    @Test
    void doFilterInternal_유효한_토큰() throws ServletException, IOException
    {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);

        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = mock(FilterChain.class);

        // When
        tokenAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertEquals("admin", authentication.getName());
    }
}
