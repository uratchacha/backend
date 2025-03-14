package com.example.lachacha.domain.chats.domain;

import com.example.lachacha.domain.user.domain.Users;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@AllArgsConstructor
@DiscriminatorValue("PRIVATE")
@SuperBuilder
public class PrivateChatRoom extends ChatRoom
{

}