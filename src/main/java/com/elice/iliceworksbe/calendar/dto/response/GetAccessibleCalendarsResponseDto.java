package com.elice.iliceworksbe.calendar.dto.response;

import com.elice.iliceworksbe.calendar.entity.Calendar;
import com.elice.iliceworksbe.common.constant.CalendarType;
import lombok.Builder;

@Builder
public record GetAccessibleCalendarsResponseDto(
        String name,
        Long calendarId,
        CalendarType calendarType,
        Long typeId,
        boolean isMyCalendar
) {

    public static GetAccessibleCalendarsResponseDto from(Calendar calendar, Long userId){
        return GetAccessibleCalendarsResponseDto.builder()
                .name(calendar.getName())
                .calendarId(calendar.getId())
                .calendarType(calendar.getType())
                .typeId(calendar.getTypeId())
                .isMyCalendar(calendar.getType() == CalendarType.MEMBER && calendar.getTypeId().equals(userId))
                .build();
    }

}
