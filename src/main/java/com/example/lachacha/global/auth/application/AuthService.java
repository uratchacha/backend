package com.example.lachacha.global.auth.application;

import com.example.lachacha.domain.user.domain.Users;
import com.example.lachacha.domain.user.domain.UsersRepository;
import com.example.lachacha.domain.user.exception.UsersException;
import com.example.lachacha.global.auth.domain.RefreshToken;
import com.example.lachacha.global.auth.domain.RefreshTokenRepository;
import com.example.lachacha.global.auth.dto.TokenResponse;
import com.example.lachacha.global.auth.jwt.TokenProvider;
import com.example.lachacha.global.exception.MyErrorCode;
import com.example.lachacha.global.exception.MyException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class AuthService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenProvider tokenProvider;
    private final UsersRepository userRepository;
    private final UsersRepository usersRepository;

    private static final Duration REFRESH_TOKEN_EXP = Duration.ofDays(1);
    private static final Duration ACCESS_TOKEN_EXP = Duration.ofHours(1);


    public Users findUsersByAuth() {
        try {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String username = ((UserDetails) principal).getUsername();
            log.info(username);
            return usersRepository.findByUsername(username);
        } catch (Exception e) {
            throw new UsersException(MyErrorCode.USER_NOT_FOUND);
        }
    }
    @Transactional
    public TokenResponse usersLogin(Users users)
    {
        refreshTokenRepository.deleteByUserId(users.getId());

        String accessToken = tokenProvider.generateToken(ACCESS_TOKEN_EXP,users);

        String refreshToken = tokenProvider.generateToken(REFRESH_TOKEN_EXP,users);
        refreshTokenRepository.save(RefreshToken.builder().refreshToken(refreshToken)
                .userId(users.getId())
                .build());
        return TokenResponse.of(accessToken, refreshToken);
    }

    public TokenResponse reissue(String refreshToken)
    {
        if(!tokenProvider.validateToken(refreshToken))
        {
            throw new MyException(MyErrorCode.INVALID_REFRESH_TOKEN);
        }

        RefreshToken storedToken = refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new MyException(MyErrorCode.REFRESH_TOKEN_NOT_FOUND));


        Users user = userRepository.findById(storedToken.getUserId())
                .orElseThrow(() -> new MyException(MyErrorCode.USER_NOT_FOUND));

        String newAccessToken = tokenProvider.generateToken(ACCESS_TOKEN_EXP, user);
        String newRefreshToken = tokenProvider.generateToken(REFRESH_TOKEN_EXP, user);

        refreshTokenRepository.deleteByUserId(user.getId());
        refreshTokenRepository.save(RefreshToken.builder()
                .userId(user.getId())
                .refreshToken(newRefreshToken)
                .build());

        return TokenResponse.of(newAccessToken, newRefreshToken);
    }


}
