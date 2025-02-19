package com.elice.iliceworksbe.notification.dto.response;

import com.elice.iliceworksbe.notification.entity.EventNotification;

import java.time.LocalDateTime;

public record EventNotificationResponseDto(String message, LocalDateTime notifyTime) {
    public static EventNotificationResponseDto from(EventNotification eventNotification) {
        return new EventNotificationResponseDto(
                eventNotification.getMessage(),
                eventNotification.getNotifyTime()
        );
    }
}
