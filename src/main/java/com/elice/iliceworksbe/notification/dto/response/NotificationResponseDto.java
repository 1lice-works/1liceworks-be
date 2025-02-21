package com.elice.iliceworksbe.notification.dto.response;

import com.elice.iliceworksbe.notification.entity.Notification;

import java.time.LocalDateTime;

public record NotificationResponseDto(Long notificationId, String message, LocalDateTime notifyTime) {
    public static NotificationResponseDto from(Notification notification) {
        return new NotificationResponseDto(
                notification.getId(),
                notification.getMessage(),
                notification.getNotifyTime()
        );
    }
}
