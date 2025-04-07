package com.elice.iliceworksbe.notification.dto.response;

import com.elice.iliceworksbe.common.constant.CalendarType;
import com.elice.iliceworksbe.notification.entity.Notification;

import java.time.LocalDateTime;

public record NotificationResponseDto(Long notificationId,
                                      String message,
                                      LocalDateTime notifyTime,
                                      LocalDateTime dtStartTime,
                                      CalendarType calendarType) {
    public static NotificationResponseDto from(Notification notification, LocalDateTime dtStartTime, CalendarType calendarType) {
        return new NotificationResponseDto(
                notification.getId(),
                notification.getMessage(),
                notification.getNotifyTime(),
                dtStartTime,
                calendarType
        );
    }
}
