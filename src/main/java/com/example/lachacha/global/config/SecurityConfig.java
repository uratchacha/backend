package com.example.lachacha.global.config;

import com.example.lachacha.global.auth.filter.TokenAuthenticationFilter;
import com.example.lachacha.global.auth.jwt.TokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.server.authorization.AuthorizationContext;

import java.util.function.Supplier;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final TokenProvider tokenProvider;
    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션 사용 X
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PUBLIC_ENDPOINTS).permitAll() // 공개 API 허용
                        .requestMatchers("/env").access(this::validateDeployToken)
                        .anyRequest().authenticated() // 나머지 요청은 인증 필요
                )
                .addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class) // JWT 필터 추가
                .build();
    }


    private static final String[] PUBLIC_ENDPOINTS = {
            "*/register",
            "*/login",
            "*/reissue",
    };


    private AuthorizationDecision validateDeployToken(Supplier<Authentication> authenticationSupplier, RequestAuthorizationContext context) {
        HttpServletRequest request = (HttpServletRequest) context.getRequest(); // 올바른 캐스팅 방식 적용
        String deployToken = request.getHeader("X-DEPLOY-TOKEN");
        String expectedToken = System.getenv("DEPLOY_SECRET_TOKEN"); // 환경변수에서 토큰 가져오기

        logger.info("Received deployToken: {}", deployToken);
        logger.info("Expected deployToken: {}", expectedToken);

        boolean isAuthorized = deployToken != null && deployToken.equals(expectedToken);
        return new AuthorizationDecision(isAuthorized);
    }

    @Bean
    public TokenAuthenticationFilter tokenAuthenticationFilter() {
        return new TokenAuthenticationFilter(tokenProvider);
    }
}

