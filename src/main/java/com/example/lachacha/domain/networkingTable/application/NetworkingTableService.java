package com.example.lachacha.domain.networkingTable.application;

import com.example.lachacha.domain.chats.application.ChatsService;
import com.example.lachacha.domain.chats.dto.response.ChatRoomResponseDto;
import com.example.lachacha.domain.networkingTable.config.NetworkingConfig;
import com.example.lachacha.domain.networkingTable.domain.NetworkingTable;
import com.example.lachacha.domain.networkingTable.domain.NetworkingTableRepository;
import com.example.lachacha.domain.networkingTable.dto.NetworkingTableRequestDto;
import com.example.lachacha.domain.networkingTable.enums.TableState;
import com.example.lachacha.domain.networkingTable.exception.NetworkingTableException;
import com.example.lachacha.domain.reservation.application.ReservationService;
import com.example.lachacha.domain.user.domain.Users;
import com.example.lachacha.global.auth.application.AuthService;
import com.example.lachacha.global.exception.MyErrorCode;
import com.example.lachacha.global.webSocket.networkingTables.TableWaitTimeHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class NetworkingTableService {

    private  final ChatsService chatsService;
    private final NetworkingTableRepository networkingTableRepository;
    private final TableWaitTimeHandler tableWaitTimeHandler;
    private final ReservationService reservationService;
    private final NetworkingConfig networkingConfig;
    private final AuthService authService;

    public NetworkingTable createTable(NetworkingTableRequestDto request) {
        NetworkingTable table = NetworkingTable.builder()
                .tableNumber(request.getTableNumber())
                .state(request.getState())
                .build();
        return networkingTableRepository.save(table);
    }
    public NetworkingTable getTableById(Long id) {
        return networkingTableRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("테이블을 찾을 수 없습니다. ID: " + id));
    }

    public NetworkingTable updateTable(Long id, NetworkingTableRequestDto request) {
        NetworkingTable table = getTableById(id);
        table.getUserIds().clear();
        return networkingTableRepository.save(table);
    }

    public void deleteTable(Long id) {
        networkingTableRepository.deleteById(id);
    }

    //테이블 사용
    public String applyForTable(Long chatRoomId) {
        ChatRoomResponseDto chatRoom = chatsService.findChatRoomById(chatRoomId);

        List<Long> userIds = chatRoom.members().stream()
                .map(Users::getId)
                .toList();

        return networkingTableRepository.findFirstByState(TableState.AVAILABLE)
                .map(table -> {
                    table.reserveTable(userIds);
                    networkingTableRepository.save(table);

                    return table.getTableNumber();
                })
                .orElseThrow(() -> new NetworkingTableException(MyErrorCode.TABLE_OCCUPIED));
    }

    // 네트워킹 시작
    @Transactional
    public void startNetworking(String tableNumber) {
        NetworkingTable table = networkingTableRepository.findByTableNumber(tableNumber)
                .orElseThrow(() -> new NetworkingTableException(MyErrorCode.TABLE_NOT_FOUND));

        Long userId = authService.findUsersByAuth().getId();
        //예약된 상태만 가능
        if(isTableNotInState(table,TableState.RESERVED)){
            throw new NetworkingTableException(MyErrorCode.TABLE_NOT_RESERVED);
        }

        //테이블에 속한 사용자만 네트워킹 시작 가능
        if (isUserNotInTable(table, userId)) {
            throw new NetworkingTableException(MyErrorCode.USER_NOT_IN_TABLE);
        }

        table.startNetworking();
        networkingTableRepository.save(table);

    }

    //네트워킹 종료
    @Transactional
    public void endNetworking(String tableNumber) {
        NetworkingTable table = networkingTableRepository.findByTableNumber(tableNumber)
                .orElseThrow(() -> new NetworkingTableException(MyErrorCode.TABLE_NOT_FOUND));

        Long userId = authService.findUsersByAuth().getId();

        //네트워킹이 진행중인 상태만 가능
        if(isTableNotInState(table,TableState.OCCUPIED)){
            throw new NetworkingTableException(MyErrorCode.NETWORKING_NOT_IN_PROGRESS);
        }

        //테이블에 속한 사용자만 네트워킹 종료 가능
        if (isUserNotInTable(table, userId)) {
            throw new NetworkingTableException(MyErrorCode.USER_NOT_IN_TABLE);
        }

        table.endNetworking();
        networkingTableRepository.save(table);

        //예약 테이블 배정
        assignmentReservationTeam();
    }

    //테이블 배정 취소
    @Transactional
    public void cancelTable(Long chatRoomId) {
        List<Users> members = chatsService.findChatRoomById(chatRoomId).members();

        // 멤버가 존재하지 않으면 예외 발생
        if (members.isEmpty()) {
            throw new NetworkingTableException(MyErrorCode.USER_NOT_FOUND);
        }

        // 멤버들의 ID 리스트 추출
        List<Long> userIds = members.stream()
                .map(Users::getId)
                .toList();

        // 해당 멤버들이 포함된 테이블 조회
        Optional<NetworkingTable> optionalTable = networkingTableRepository.findFirstByUserIdsContains(userIds.get(0));

        // 테이블이 존재하지 않으면 예외 발생
        NetworkingTable table = optionalTable.orElseThrow(() -> new NetworkingTableException(MyErrorCode.TABLE_NOT_FOUND));

        //테이블이 배정된 상태만 가능
        if(isTableNotInState(table,TableState.RESERVED)){
            throw new NetworkingTableException(MyErrorCode.TABLE_NOT_RESERVED);
        }

        table.endNetworking();
        networkingTableRepository.save(table);

        assignmentReservationTeam();
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


    //예상 대기시간 List
    public List<Integer> calculateEstimatedWaitTimes() {
        // 1. 사용 가능한 테이블이 하나라도 있다면 대기시간 없이 빈 리스트 반환
        if (networkingTableRepository.existsByState(TableState.AVAILABLE)) {
            return Collections.emptyList();
        }

        // 2. RESERVED 또는 OCCUPIED 상태의 테이블 조회
        List<NetworkingTable> activeTables = networkingTableRepository.findByStateIn(
                List.of(TableState.RESERVED, TableState.OCCUPIED)
        );

        // 3. 네트워킹 시작 시간을 기준으로 정렬 (null 값은 가장 마지막으로 정렬)
        activeTables.sort(Comparator.comparing(
                table -> table.getStartTime() != null ? table.getStartTime() : LocalDateTime.MAX
        ));

        List<Integer> waitTimes = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        Duration networkingDuration = networkingConfig.getDuration();

        for (NetworkingTable table : activeTables) {
            if (table.getStartTime() == null) {
                waitTimes.add(networkingDuration.toMinutesPart()); // 아직 시작 안된 경우, 전체 네트워킹 시간 적용
            } else {
                long remainingTime = ChronoUnit.MINUTES.between(now, table.getStartTime().plus(networkingDuration));
                waitTimes.add((int) Math.max(remainingTime, 0)); // 음수일 경우 0 처리
            }
        }

        return waitTimes;
    }

    public Duration getNetworkingDuration(){
        return networkingConfig.getDuration();
    }

    // 예약 팀 배정
    public void assignmentReservationTeam(){
        Long firstChatRoomId = tableWaitTimeHandler.getFirstChatRoomId();
        if( firstChatRoomId != null){
            //테이블 배정
            applyForTable(firstChatRoomId);

            //예약 상태 변경
            reservationService.assignReservationToTable(firstChatRoomId);

            //예약 맵 삭제
            tableWaitTimeHandler.removeReservationById(firstChatRoomId);
        }
    }
}