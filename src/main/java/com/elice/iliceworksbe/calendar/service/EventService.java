package com.elice.iliceworksbe.calendar.service;

import com.elice.iliceworksbe.calendar.dto.request.PostMyEventRequestDto;
import com.elice.iliceworksbe.calendar.dto.request.PostTeamEventRequestDto;
import com.elice.iliceworksbe.calendar.dto.response.EventJsonResponseDto;
import com.elice.iliceworksbe.calendar.entity.Event;

import java.time.LocalDate;
import java.util.List;

public interface EventService {
    void postTeamEvent(Long userId, Long calendarId, PostTeamEventRequestDto postTeamEventRequestDto);
    void postMyEvent(Long userId, PostMyEventRequestDto postMyEventRequestDto);
    List<EventJsonResponseDto> getEventsByDateAndParticipants(Long teamCalendarId, LocalDate date, List<Long> userIds);
}
