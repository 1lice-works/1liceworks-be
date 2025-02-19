package com.elice.iliceworksbe.notification.dto.request;


import java.time.LocalDateTime;

public record EventNotificationRequestDto(String message, LocalDateTime notifyTime, String username, Long eventId) {
}
