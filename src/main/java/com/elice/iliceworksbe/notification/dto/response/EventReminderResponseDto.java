package com.elice.iliceworksbe.notification.dto.response;

import com.elice.iliceworksbe.notification.entity.EventReminder;

import java.time.LocalDateTime;

public record EventReminderResponseDto(Long id, LocalDateTime notifyTime) {
    public static EventReminderResponseDto from(EventReminder eventReminder) {
        return new EventReminderResponseDto(
                eventReminder.getId(),
                eventReminder.getNotifyTime()
        );
    }
}
