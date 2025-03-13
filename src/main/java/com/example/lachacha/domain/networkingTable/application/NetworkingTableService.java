package com.example.lachacha.domain.networkingTable.application;

import com.example.lachacha.domain.networkingTable.config.NetworkingConfig;
import com.example.lachacha.domain.networkingTable.domain.NetworkingTable;
import com.example.lachacha.domain.networkingTable.domain.NetworkingTableRepository;
import com.example.lachacha.domain.networkingTable.dto.NetworkingRequestDto;
import com.example.lachacha.domain.networkingTable.enums.TableState;
import com.example.lachacha.domain.networkingTable.exception.NetworkingTableException;
import com.example.lachacha.domain.reservation.application.ReservationService;
import com.example.lachacha.global.exception.MyErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class NetworkingTableService {

    private final NetworkingConfig networkingConfig;
    private final NetworkingTableRepository networkingTableRepository;
    private final ReservationService reservationService;

    // 네트워킹 자동 종료 스레드를 저장
    private final ConcurrentHashMap<Long, Thread> activeThreads = new ConcurrentHashMap<>();

    //테이블 사용
    public void applyForTable(List<Long> userIds) {

        networkingTableRepository.findFirstByState(TableState.AVAILABLE)
                .ifPresentOrElse(table -> {
                    table.reserveTable(userIds);
                    networkingTableRepository.save(table);
                }, () -> {
                  //  reservationService.updateWaitTime(); // 대기시간 갱신
                    throw new NetworkingTableException(MyErrorCode.TABLE_OCCUPIED);
                }); // 대기시간 갱신
    }

    // 네트워킹 시작
    @Transactional
    public void startNetworking(NetworkingRequestDto requestDto) {
        NetworkingTable table = networkingTableRepository.findByTableNumber(requestDto.getTableNumber())
                .orElseThrow(() -> new NetworkingTableException(MyErrorCode.TABLE_NOT_FOUND));

        //예약된 상태만 가능
        if(isTableNotInState(table,TableState.RESERVED)){
            throw new NetworkingTableException(MyErrorCode.TABLE_NOT_RESERVED);
        }

        //테이블에 속한 사용자만 네트워킹 시작 가능
        if (isUserNotInTable(table, requestDto.getUserId())) {
            throw new NetworkingTableException(MyErrorCode.USER_NOT_IN_TABLE);
        }



        table.startNetworking();
        networkingTableRepository.save(table);

        scheduleAutoEndNetworking(table.getId());
    }

    //네트워킹 종료
    @Transactional
    public void endNetworking(NetworkingRequestDto requestDto) {
        NetworkingTable table = networkingTableRepository.findByTableNumber(requestDto.getTableNumber())
                .orElseThrow(() -> new NetworkingTableException(MyErrorCode.TABLE_NOT_FOUND));

        //네트워킹이 진행중인 상태만 가능
        if(isTableNotInState(table,TableState.OCCUPIED)){
            throw new NetworkingTableException(MyErrorCode.NETWORKING_NOT_IN_PROGRESS);
        }

        //테이블에 속한 사용자만 네트워킹 종료 가능
        if (isUserNotInTable(table, requestDto.getUserId())) {
            throw new NetworkingTableException(MyErrorCode.USER_NOT_IN_TABLE);
        }

        // 자동 종료 스레드가 실행 중이면 중단
        if (activeThreads.containsKey(table.getId())) {
            Thread autoEndThread = activeThreads.get(table.getId());
            autoEndThread.interrupt(); // 스레드 중단
            activeThreads.remove(table.getId()); // 맵에서 삭제
        }

        table.endNetworking();
        networkingTableRepository.save(table);
    }

    //자동종료 카운트 시작
    @Async
    public void scheduleAutoEndNetworking(Long tableId) {
        Thread currentThread = Thread.currentThread();
        activeThreads.put(tableId, currentThread); // 현재 실행 중인 스레드를 저장

        try {

            Thread.sleep(networkingConfig.getDuration().toMillis());

            NetworkingTable table = networkingTableRepository.findById(tableId)
                    .orElseThrow(() -> new NetworkingTableException(MyErrorCode.TABLE_NOT_FOUND));

            // 종료 로직 실행
            table.endNetworking();
            networkingTableRepository.save(table);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();

        }finally {
            activeThreads.remove(tableId);
        }
    }

    //테이블 유저 검증
    private boolean isUserNotInTable(NetworkingTable table, Long userId) {
        return !table.getUserIds().contains(userId);
    }

    //테이블 상태 검증
    private boolean isTableNotInState(NetworkingTable table, TableState state) {
        return !table.getState().equals(state);
    }

    //네트워킹 시간 변경(분 단위)
    public void setNetworkingDuration(long newDurationInMinutes) {
        networkingConfig.setDuration(Duration.ofMinutes(newDurationInMinutes));
    }




}
