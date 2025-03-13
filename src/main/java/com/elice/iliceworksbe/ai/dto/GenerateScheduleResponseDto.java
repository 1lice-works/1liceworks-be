package com.elice.iliceworksbe.ai.dto;

import com.elice.iliceworksbe.common.constant.Availability;
import com.elice.iliceworksbe.common.constant.PrivacyType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GenerateScheduleResponseDto {
    @JsonProperty("title")
    private String title;

    @JsonProperty("description")
    private String description;

    @JsonProperty("dtStartTime")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dtStartTime;

    @JsonProperty("dtEndTime")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dtEndTime;

    @JsonProperty("isAllDay")
    private Boolean isAllDay;

    @JsonProperty("privacyType")
    private PrivacyType privacyType;

    @JsonProperty("availability")
    private Availability availability;

    @JsonProperty("location")
    private String location;

    @JsonProperty("eventReminders")
    private List<EventReminderDto> eventReminders;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EventReminderDto {
        @JsonProperty("notifyTime")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime notifyTime;
    }
}
