package com.elice.iliceworksbe.notification.service;

import com.elice.iliceworksbe.notification.dto.request.EventReminderRequestDto;
import com.elice.iliceworksbe.notification.dto.response.EventReminderResponseDto;

import java.util.List;

public interface EventReminderService {
    List<EventReminderResponseDto> postEventReminder(Long eventId, List<EventReminderRequestDto> requestDtos);
    List<EventReminderResponseDto> getEventReminder(Long eventId);
    void deleteAllEventReminderByEventId(Long eventId);
    void checkEventReminder();
}
