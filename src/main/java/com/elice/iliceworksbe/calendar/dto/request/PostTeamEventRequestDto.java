package com.elice.iliceworksbe.calendar.dto.request;

import com.elice.iliceworksbe.common.constant.Availability;
import com.elice.iliceworksbe.common.constant.PrivacyType;

import java.time.LocalDateTime;
import java.util.List;

public record PostTeamEventRequestDto(
        String title,
        String description,
        LocalDateTime dtStartTime,
        LocalDateTime dtEndTime,
        Boolean isAllDay,
        PrivacyType privacyType,
        Availability availability,
        String location,
        List<EventReminderDto> eventReminders,
        List<EventParticipantDto> eventParticipants
) {

    public record EventReminderDto(
        LocalDateTime notifyTime
    ) {}

    public record EventParticipantDto(
       Long userId
    ) {}
}
