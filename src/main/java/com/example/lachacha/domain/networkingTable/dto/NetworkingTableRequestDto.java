package com.example.lachacha.domain.networkingTable.dto;

import com.example.lachacha.domain.networkingTable.enums.TableState;
import lombok.Getter;

import java.util.List;

@Getter
public class NetworkingTableRequestDto {
    private String tableNumber;
    private TableState state;
    private List<Long> userIds;
}
