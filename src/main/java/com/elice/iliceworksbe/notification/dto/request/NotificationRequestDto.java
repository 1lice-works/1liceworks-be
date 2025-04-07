package com.elice.iliceworksbe.notification.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record NotificationRequestDto(
        @NotNull(message = "userId는 필수 입력 값입니다")
        Long userId,
        @NotBlank(message = "message는 필수 입력 값입니다")
        String message,
        Long eventId,
        Long calendarId
) {}
