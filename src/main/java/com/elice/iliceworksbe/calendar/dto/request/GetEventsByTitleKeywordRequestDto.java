package com.elice.iliceworksbe.calendar.dto.request;

import java.util.List;

public record GetEventsByTitleKeywordRequestDto(
        String keyword,
        List<Long> calendarIds
) {
}
