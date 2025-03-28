package com.example.lachacha.domain.chats.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long>
{
    @Query("SELECT c FROM ChatRoom c WHERE c.chatroomType  = 'GROUP'")
    List<ChatRoom> findGroupChatRooms();
}
