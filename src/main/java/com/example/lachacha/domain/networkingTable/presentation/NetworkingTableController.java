package com.example.lachacha.domain.networkingTable.presentation;


import com.example.lachacha.domain.networkingTable.application.NetworkingTableService;
import com.example.lachacha.domain.networkingTable.domain.NetworkingTable;
import com.example.lachacha.domain.networkingTable.dto.NetworkingRequestDto;
import com.example.lachacha.domain.networkingTable.dto.NetworkingTableRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/networking-table")
@RequiredArgsConstructor
public class NetworkingTableController {

    private final NetworkingTableService networkingTableService;

    //테이블 생성
    @PostMapping
    public ResponseEntity<NetworkingTable> createTable(@RequestBody NetworkingTableRequestDto request) {
        return ResponseEntity.ok(networkingTableService.createTable(request));
    }
    //테이블 수정
    @PutMapping("/{id}")
    public ResponseEntity<NetworkingTable> updateTable(@PathVariable Long id, @RequestBody NetworkingTableRequestDto request) {
        return ResponseEntity.ok(networkingTableService.updateTable(id, request));
    }
    //테이블 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTable(@PathVariable Long id) {
        networkingTableService.deleteTable(id);
        return ResponseEntity.noContent().build();
    }
    //네트워킹 시작
    @PostMapping("/start")
    public ResponseEntity<Void> startNetworking(@RequestBody NetworkingRequestDto requestDto) {
        networkingTableService.startNetworking(requestDto);
        return ResponseEntity.ok().build();
    }
    //네트워킹 종료
    @PostMapping("/end")
    public ResponseEntity<Void> endNetworking(@RequestBody NetworkingRequestDto requestDto) {
        networkingTableService.endNetworking(requestDto);
        return ResponseEntity.ok().build();
    }

    //테이블 신청
    @PostMapping("/apply/{chatRoomId}")
    public ResponseEntity<String> applyForTable(@PathVariable("chatRoomId") Long chatRoomId) {
        String tableNumber = networkingTableService.applyForTable(chatRoomId);
        return ResponseEntity.ok(tableNumber);
    }

    //테이블 취소
    @PostMapping("/cancel/{chatRoomId}")
    public ResponseEntity<String> cancelTable(@PathVariable("chatRoomId") Long chatRoomId) {
        networkingTableService.cancelTable(chatRoomId);
        return ResponseEntity.ok().build();
    }


    // 네트워킹 시간 변경 (분 단위)
    @PatchMapping("/duration/{newDuration}")
    public ResponseEntity<Void> updateNetworkingDuration(@PathVariable("newDuration") long newDuration) {
        networkingTableService.setNetworkingDuration(newDuration);
        return ResponseEntity.ok().build();
    }
}
