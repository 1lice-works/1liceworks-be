package com.elice.iliceworksbe.calendar.dto.request;

import com.elice.iliceworksbe.common.constant.Availability;
import com.elice.iliceworksbe.common.constant.PrivacyType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public record PatchMyEventRequestDto(
        @NotBlank(message = "일정 제목은 필수입니다.")
        String title,
        String description,
        @NotNull(message = "일정 시작 시간 설정을 해주세요.")
        LocalDateTime dtStartTime,
        @NotNull(message = "일정 종료 시간 설정을 해주세요.")
        LocalDateTime dtEndTime,
        @NotBlank(message = "종일 설정을 해주세요.")
        Boolean isAllDay,
        @NotNull
        PrivacyType privacyType,
        @NotNull
        Availability availability,
        String location,
        List<EventReminderDto> eventReminders
) {

    public record EventReminderDto(
        LocalDateTime notifyTime
    ) {}
}
