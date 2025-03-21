package com.example.lachacha.domain.networkingTable.domain;

import com.example.lachacha.domain.networkingTable.enums.TableState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface NetworkingTableRepository extends JpaRepository<NetworkingTable, Long> {
    // 사용 가능한 첫 번째 테이블 조회
    Optional<NetworkingTable> findFirstByState(TableState state);

    // 테이블 번호로 테이블 조회
    Optional<NetworkingTable> findByTableNumber(String tableNumber);

    // 특정 상태의 테이블 개수 조회
    long countByState(TableState state);

    // 특정 상태의 테이블 존재 여부 확인
    boolean existsByState(TableState state);

    // 특정 상태 목록의 테이블 조회
    List<NetworkingTable> findByStateIn(List<TableState> states);

    // 테이블 ID로 조회
    Optional<NetworkingTable> findById(Long id);

    // 여러 사용자 ID가 포함된 테이블 찾기
    Optional<NetworkingTable> findFirstByUserIdsContains(Long userId);
}
