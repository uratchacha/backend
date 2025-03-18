package com.example.lachacha.domain.user.domain;

import com.example.lachacha.domain.chats.domain.ChatRoom;
import com.example.lachacha.global.exception.MyErrorCode;
import com.example.lachacha.global.exception.MyException;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;


import java.util.Collection;
import java.util.Collections;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
public class Users implements UserDetails
{
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, length = 100)
    private String password;

    @Column(nullable = false)
    private boolean isParticipate=true;

    @Column(nullable = false)
    private boolean notificationsEnabled=true;

    @Column(nullable = false)
    private String introduction;

    @Column(nullable = false)
    private String roles;

    @Column(nullable = false)
    private String interests;

    @Column(nullable = false)
    private String participationPurpose;

    @Column(nullable = false)
    private String additionalNotificationMethods;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
    }
    public boolean isPasswordMatch(String inputPassword, PasswordEncoder passwordEncoder) {
        return passwordEncoder.matches(inputPassword, this.password);
    }

    public void setChatRoom(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
    }
}
