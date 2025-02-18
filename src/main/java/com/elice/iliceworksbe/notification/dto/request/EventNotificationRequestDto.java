package com.elice.iliceworksbe.notification.dto.request;

import com.elice.iliceworksbe.calendar.entity.Event;

import java.time.LocalDateTime;

public record EventNotificationRequestDto(String message, LocalDateTime notifyTime, String username, Long eventId) {
}
