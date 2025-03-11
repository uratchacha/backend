package com.example.lachacha.domain.chats.domain;

import com.example.lachacha.domain.chats.exception.ChatsException;
import com.example.lachacha.domain.user.domain.Users;
import com.example.lachacha.global.exception.MyErrorCode;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;


@SuperBuilder // Lombok의 SuperBuilder 적용
@Entity
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "chatroom_type")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class ChatRoom
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int maxSize;

    @OneToMany(mappedBy = "chatRoom")
    @Builder.Default
    private List<Users> members=new ArrayList<>();

    public void addMember(Users user) {
        if (members.size() < maxSize) {
            members.add(user);
        } else {
            throw new ChatsException(MyErrorCode.INVALID_INPUT);
        }
    }

    public void removeMember(Users user) {
        members.remove(user);
    }
}
