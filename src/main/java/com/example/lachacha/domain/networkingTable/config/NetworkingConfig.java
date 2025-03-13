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

    @DurationUnit(ChronoUnit.MILLIS) // 자동으로 밀리초로 변환
    private Duration duration;
}
