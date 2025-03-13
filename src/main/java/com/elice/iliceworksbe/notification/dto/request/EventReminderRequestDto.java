package com.elice.iliceworksbe.notification.dto.request;


import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record EventReminderRequestDto(
        @NotNull(message = "알림시간은 필수 입력값입니다")
        @Future(message = "알림시간은 과거일 수 없습니다")
        LocalDateTime notifyTime
) {}
