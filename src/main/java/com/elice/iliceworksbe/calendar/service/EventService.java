package com.elice.iliceworksbe.calendar.service;

import com.elice.iliceworksbe.calendar.dto.request.PostTeamEventRequestDto;

public interface EventService {
    void postTeamEvent(Long userId, Long calendarId, PostTeamEventRequestDto postTeamEventRequestDto);
}
