package com.example.lachacha.domain.user.application;

import com.example.lachacha.domain.user.domain.Users;
import com.example.lachacha.domain.user.domain.UsersRepository;
import com.example.lachacha.domain.user.dto.request.UserUpdateRequestDto;
import com.example.lachacha.domain.user.dto.request.UsersLoginRequest;
import com.example.lachacha.domain.user.dto.request.UsersRequestDto;
import com.example.lachacha.domain.user.dto.response.MyPageResponseDto;
import com.example.lachacha.domain.user.dto.response.UserProfileDto;
import com.example.lachacha.domain.user.exception.UsersException;
import com.example.lachacha.global.auth.application.AuthService;
import com.example.lachacha.global.auth.dto.TokenResponse;
import com.example.lachacha.global.exception.MyErrorCode;
import com.example.lachacha.global.exception.MyException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

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

    public boolean isUsernameAvailable(String username)
    {
        return !usersRepository.existsByUsername(username);
    }
    @Transactional
    public void create(UsersRequestDto usersRequestDto)
    {

        Users users= usersRequestDto.toEntity(bCryptPasswordEncoder);
        long number= usersRepository.countByJobCategory(users.getJobCategory());
        users.makeNickName(number);
        usersRepository.save(users);
    }

    @Transactional
    public TokenResponse login(UsersLoginRequest usersLoginRequest)
    {
        Users users=usersRepository.findByUsername(usersLoginRequest.username());
        if(!users.isPasswordMatch(usersLoginRequest.password(),bCryptPasswordEncoder))
        {
            throw new MyException(MyErrorCode.PASSWORD_NOT_MATCH);
        }
        return authService.usersLogin(users);

    }

    public Long countUsersByInterests()
    {
        Users users=authService.findUsersByAuth();
        return usersRepository.countByInterestsContaining(users.getJobCategory(),users.getJobValue());
    }

    public void updateIsParticipate()
    {
        Users users=authService.findUsersByAuth();
        users.updateIsParticipate();
    }

    public void updateNotificationsEnabled()
    {
        Users users=authService.findUsersByAuth();
        users.updateNotificationsEnabled();
    }

    public MyPageResponseDto myPage()
    {
        return MyPageResponseDto.of(authService.findUsersByAuth());
    }

    public List<UserProfileDto> getAllUsers()
    {
        List<Users> users=usersRepository.findByIsParticipateTrue();
        List<UserProfileDto> userProfileDtos=new ArrayList<>();
        for(Users user:users)
            userProfileDtos.add(UserProfileDto.of(user));
        return userProfileDtos;
    }

    @Transactional
    public void updateUser(UserUpdateRequestDto requestDto)
    {
        Users user=authService.findUsersByAuth();
        Users updatedUser=user.updateUserInfo(requestDto);
        usersRepository.save(updatedUser);
    }
}
