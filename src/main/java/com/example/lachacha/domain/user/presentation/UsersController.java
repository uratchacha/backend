package com.example.lachacha.domain.user.presentation;

import com.example.lachacha.domain.user.application.UsersService;
import com.example.lachacha.domain.user.dto.request.UserUpdateRequestDto;
import com.example.lachacha.domain.user.dto.request.UsersLoginRequest;
import com.example.lachacha.domain.user.dto.request.UsersRequestDto;
import com.example.lachacha.domain.user.dto.response.MyPageResponseDto;
import com.example.lachacha.domain.user.dto.response.UserProfileDto;
import com.example.lachacha.global.auth.dto.TokenResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UsersController {

    private final UsersService userService;

    // 회원가입 API
    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@RequestBody UsersRequestDto usersRequestDto) {
        userService.create(usersRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // 로그인 API
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody UsersLoginRequest usersLoginRequest,
                                               HttpServletResponse response) {
        TokenResponse tokenResponse = userService.login(usersLoginRequest);

        Cookie cookie = new Cookie("access_token", tokenResponse.accessToken());
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60);

        response.addCookie(cookie);
        return ResponseEntity.ok(tokenResponse);
    }

    // 관심사로 유저 수 조회 API
    @GetMapping("/countByInterests")
    public ResponseEntity<Long> countUsersByInterests() {
        Long count = userService.countUsersByInterests();
        return ResponseEntity.ok(count);
    }

    // 참여 여부 업데이트 API
    @PutMapping("/updateParticipate")
    public ResponseEntity<Void> updateIsParticipate() {
        userService.updateIsParticipate();
        return ResponseEntity.ok().build();
    }

    // 알림 수신 여부 업데이트 API
    @PutMapping("/updateNotifications")
    public ResponseEntity<Void> updateNotificationsEnabled() {
        userService.updateNotificationsEnabled();
        return ResponseEntity.ok().build();
    }

    // 마이페이지 조회 API
    @GetMapping("/mypage")
    public ResponseEntity<MyPageResponseDto> myPage() {
        MyPageResponseDto myPageResponseDto = userService.myPage();
        return ResponseEntity.ok(myPageResponseDto);
    }

    // 모든 유저 조회 API
    @GetMapping("/all")
    public ResponseEntity<List<UserProfileDto>> getAllUsers() {
        List<UserProfileDto> userProfileDtos = userService.getAllUsers();
        return ResponseEntity.ok(userProfileDtos);
    }

    @GetMapping("/check-username")
    public ResponseEntity<Boolean> checkUsername(@RequestParam String username) {
        boolean isAvailable = userService.isUsernameAvailable(username);
        return ResponseEntity.ok(isAvailable);
    }

    @PatchMapping()
    public ResponseEntity<Void> updateProfile(@RequestBody UserUpdateRequestDto usersRequestDto) {
        userService.updateUser(usersRequestDto);
        return ResponseEntity.ok().build();
    }
}