package com.example.demo.Api.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.ZoneId;

@Configuration
public class ClockConfig {

    // "Today" for the business rules is the date in the university's time zone, not the container's (UTC).
    @Bean
    public Clock clock(@Value("${app.time-zone:America/Bogota}") String timeZone) {
        return Clock.system(ZoneId.of(timeZone));
    }
}
