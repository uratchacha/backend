package com.example.lachacha.domain.networkingTable.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationUnit;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.temporal.ChronoUnit;


@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "networking")
public class NetworkingConfig {

    @DurationUnit(ChronoUnit.MILLIS)
    private Duration duration;  //네트워킹 시간 (분 단위)

    @DurationUnit(ChronoUnit.MILLIS) //네트워킹 시작 전 대기 시간 (분 단위)
    private Duration waitingDuration;
}