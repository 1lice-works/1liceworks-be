package com.elice.iliceworksbe.calendar.config.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Data
@Configuration
@ConfigurationProperties(prefix = "google")
public class GoogleCalendarProperty {
    private String apiKey;
    private String googleKoreaHolidayId;
}