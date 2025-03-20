package com.example.lachacha.domain.user.domain;

import com.example.lachacha.domain.chats.domain.ChatRoom;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

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

    @Column(length = 100)
    private String name;

    @Column(unique = true, length = 100)
    private String email; // 이메일

    @Column(length = 50)
    private String affiliation;

    @Column(length = 50)
    private String nickName;

    @Column(length = 100)
    private String career; // 경력

    private String contactInfo;

    private String interestJobCategory;

    private String interestJobValue;

    @ElementCollection
    @Builder.Default
    private List<String> interests=new ArrayList<>();

    private String jobCategory;

    private String jobValue;

    private String participationPurpose;


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

    public void updateChatRoom(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
    }

    public void updateIsParticipate()
    {
        this.isParticipate=!isParticipate;
    }
    public void updateNotificationsEnabled()
    {
        this.notificationsEnabled=!notificationsEnabled;
    }

    public void makeNickName(long number) {
        this.nickName=jobCategory+(number+1);
    }
}
