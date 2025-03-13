package com.example.lachacha.domain.networkingTable.domain;

import com.example.lachacha.domain.networkingTable.enums.TableState;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "networking_table")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class NetworkingTable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 테이블 ID

    @Column(nullable = false, unique = true)
    private String tableNumber; // 테이블 번호(이름)

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TableState state; // 대기중, 배정됨, 진행중

    @Column
    private LocalDateTime startTime; // 네트워킹 시작 시간

    @ElementCollection
    @CollectionTable(name = "networking_table_users", joinColumns = @JoinColumn(name = "networking_table_id"))
    @Column(name = "user_id")
    private List<Long> userIds = new ArrayList<>(); // 사용자 ID 목록

    public void startNetworking() {
        this.state = TableState.OCCUPIED;
        this.startTime = LocalDateTime.now();
    }

    public void endNetworking() {
        this.state = TableState.AVAILABLE;
        this.userIds.clear();
        this.startTime = null;
    }

    public void reserveTable(List<Long> userIds) {
        this.state = TableState.RESERVED;
        this.userIds.addAll(userIds);
    }

}