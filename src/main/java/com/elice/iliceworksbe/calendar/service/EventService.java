package com.elice.iliceworksbe.calendar.service;

import com.elice.iliceworksbe.calendar.dto.request.*;
import com.elice.iliceworksbe.calendar.dto.response.GetAccessibleCalendarsResponseDto;
import com.elice.iliceworksbe.calendar.dto.response.GetCalendarEventsResponseDto;
import com.elice.iliceworksbe.calendar.dto.response.GetEventsByTitleKeywordResponseDto;
import com.elice.iliceworksbe.common.constant.CalendarType;

import java.util.List;
import com.elice.iliceworksbe.calendar.dto.response.EventJsonResponseDto;

import java.time.LocalDate;

public interface EventService {
    void postTeamEvent(Long userId, Long calendarId, PostTeamEventRequestDto postTeamEventRequestDto);
    void postMyEvent(Long userId, PostMyEventRequestDto postMyEventRequestDto);

    GetCalendarEventsResponseDto getCalendarEvents(Long requestingUserId, Long targetUserId, Long calendarId, int targetMonth, int targetYear, CalendarType calendarType);

    List<GetAccessibleCalendarsResponseDto> getAccessibleCalendars(Long userId);

    void deleteTeamEvent(Long userId, Long calendarId, Long eventId);
    void deleteMyEvent(Long userId, Long eventId);

    void patchTeamEvent(Long userId, Long calendarId, Long eventId, PatchTeamEventRequestDto patchTeamEventRequestDto);
    void patchMyEvent(Long userId, Long eventId, PatchMyEventRequestDto patchMyEventRequestDto);

    GetEventsByTitleKeywordResponseDto getEventsByTitleKeyword(Long userId, GetEventsByTitleKeywordRequestDto getEventsByTitleKeywordRequestDto);

    List<EventJsonResponseDto> getEventsByDateAndParticipants(Long teamCalendarId, LocalDate date, List<Long> userIds);
}
