package com.example.lachacha.domain.user.application;

import com.example.lachacha.domain.user.domain.Users;
import com.example.lachacha.domain.user.domain.UsersRepository;
import com.example.lachacha.domain.user.exception.UsersException;
import com.example.lachacha.global.exception.MyErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsersService
{
    private final UsersRepository usersRepository;

    public Users findUsersById(Long id)
    {
        return usersRepository.findById(id).orElseThrow(() -> new UsersException(MyErrorCode.USER_NOT_FOUND));
    }
}
