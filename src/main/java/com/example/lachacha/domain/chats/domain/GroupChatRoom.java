package com.example.lachacha.domain.chats.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
@DiscriminatorValue("GROUP")
@SuperBuilder
public class GroupChatRoom extends ChatRoom
{
    private List<String> job; // null 가능
    private String career; // null 가능
    private String interests; // null 가능
    private String participationPurpose; // null 가능

}
