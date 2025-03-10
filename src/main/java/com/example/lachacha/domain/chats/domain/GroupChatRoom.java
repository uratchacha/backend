package com.example.lachacha.domain.chats.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@AllArgsConstructor
@DiscriminatorValue("GROUP")
@SuperBuilder
public class GroupChatRoom extends ChatRoom
{

}
