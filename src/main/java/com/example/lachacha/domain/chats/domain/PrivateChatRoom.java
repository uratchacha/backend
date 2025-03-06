package com.example.lachacha.domain.chats.domain;

import com.example.lachacha.domain.user.domain.Users;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@DiscriminatorValue("PRIVATE")
public class PrivateChatRoom extends ChatRoom
{

    @OneToOne(fetch = FetchType.EAGER)
    private Users user1;

    @OneToOne(fetch = FetchType.EAGER)
    private Users user2;

}