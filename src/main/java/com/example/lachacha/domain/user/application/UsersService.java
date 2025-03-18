package com.example.lachacha.domain.user.application;

import com.example.lachacha.domain.user.domain.Users;
import com.example.lachacha.domain.user.domain.UsersRepository;
import com.example.lachacha.domain.user.dto.request.UsersLoginRequest;
import com.example.lachacha.domain.user.dto.request.UsersRequestDto;
import com.example.lachacha.domain.user.dto.response.UsersResponseDto;
import com.example.lachacha.domain.user.exception.UsersException;
import com.example.lachacha.global.auth.application.AuthService;
import com.example.lachacha.global.auth.domain.RefreshToken;
import com.example.lachacha.global.auth.dto.TokenResponse;
import com.example.lachacha.global.exception.MyErrorCode;
import com.example.lachacha.global.exception.MyException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UsersService
{
    private final UsersRepository usersRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final AuthService authService;

    public Users findUsersById(Long id)
    {
        return usersRepository.findById(id).orElseThrow(() -> new UsersException(MyErrorCode.USER_NOT_FOUND));
    }

    @Transactional
    public UsersResponseDto create(UsersRequestDto usersRequestDto)
    {
        if(usersRepository.existsByUsername(usersRequestDto.username()))
        {
            throw new UsersException(MyErrorCode.NOTIFICATION_ERROR);
        }
        Users users=Users.builder()
                    .username(usersRequestDto.username())
                    .password(bCryptPasswordEncoder.encode(usersRequestDto.password()))
                    .introduction(usersRequestDto.introduction())
                    .roles(usersRequestDto.roles())
                    .interests(usersRequestDto.interests())
                    .participationPurpose(usersRequestDto.participationPurpose())
                    .additionalNotificationMethods(usersRequestDto.additionalNotificationMethods())
                    .build();
        usersRepository.save(users);
        return UsersResponseDto.of(users);
    }

    public TokenResponse login(UsersLoginRequest usersLoginRequest)
    {
        Users users=usersRepository.findByUsername(usersLoginRequest.username());
        if(!users.isPasswordMatch(usersLoginRequest.password(),bCryptPasswordEncoder))
        {
            throw new MyException(MyErrorCode.PASSWORD_NOT_MATCH);
        }
        return authService.usersLogin(users);

    }
}
