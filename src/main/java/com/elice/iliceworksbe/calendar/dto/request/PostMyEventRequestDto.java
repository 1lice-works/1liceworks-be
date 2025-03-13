package com.elice.iliceworksbe.calendar.dto.request;

import com.elice.iliceworksbe.ai.dto.GenerateScheduleResponseDto;
import com.elice.iliceworksbe.common.constant.Availability;
import com.elice.iliceworksbe.common.constant.PrivacyType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Builder
public record PostMyEventRequestDto(
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

    public static PostMyEventRequestDto from(GenerateScheduleResponseDto responseDto) {
        return PostMyEventRequestDto.builder()
                .title(responseDto.getTitle())
                .description(responseDto.getDescription())
                .dtStartTime(responseDto.getDtStartTime())
                .dtEndTime(responseDto.getDtEndTime())
                .isAllDay(responseDto.getIsAllDay())
                .privacyType(responseDto.getPrivacyType())
                .availability(responseDto.getAvailability())
                .location(responseDto.getLocation())
                .eventReminders(responseDto.getEventReminders().stream()
                                .map(reminder -> new EventReminderDto(reminder.getNotifyTime()))
                                .collect(Collectors.toList())
                )
                .build();
    }
}
