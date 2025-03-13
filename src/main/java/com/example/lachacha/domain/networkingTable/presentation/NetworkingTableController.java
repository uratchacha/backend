package com.example.lachacha.domain.networkingTable.presentation;


import com.example.lachacha.domain.networkingTable.application.NetworkingTableService;
import com.example.lachacha.domain.networkingTable.dto.NetworkingRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/networking")
@RequiredArgsConstructor
public class NetworkingTableController {

    private final NetworkingTableService networkingTableService;

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
    @PostMapping("/apply")
    public ResponseEntity<Void> applyForTable(@RequestBody List<Long> userIds) {
        networkingTableService.applyForTable(userIds);
        return ResponseEntity.ok().build();
    }


    // 네트워킹 시간 변경 (분 단위)
    @PatchMapping("/duration/{newDuration}")
    public ResponseEntity<Void> updateNetworkingDuration(@PathVariable("newDuration") long newDuration) {
        networkingTableService.setNetworkingDuration(newDuration);
        return ResponseEntity.ok().build();
    }
}
