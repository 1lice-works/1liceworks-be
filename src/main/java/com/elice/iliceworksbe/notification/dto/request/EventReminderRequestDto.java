package com.elice.iliceworksbe.notification.dto.request;


import java.time.LocalDateTime;

public record EventReminderRequestDto(LocalDateTime notifyTime) {
}
