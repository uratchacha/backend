package com.example.lachacha.domain.networkingTable.domain;

import com.example.lachacha.domain.networkingTable.enums.TableState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NetworkingTableRepository extends JpaRepository<NetworkingTable, Long> {
    Optional<NetworkingTable> findFirstByState(TableState state);

    Optional<NetworkingTable> findByTableNumber(String tableNumber);
}
