package com.elice.iliceworksbe.notification.service.impl;

import com.elice.iliceworksbe.calendar.entity.Event;
import com.elice.iliceworksbe.calendar.repository.EventRepository;
import com.elice.iliceworksbe.common.exception.BaseException;
import com.elice.iliceworksbe.common.exception.ErrorCode;
import com.elice.iliceworksbe.notification.dto.request.EventReminderRequestDto;
import com.elice.iliceworksbe.notification.dto.response.EventReminderResponseDto;
import com.elice.iliceworksbe.notification.entity.EventReminder;
import com.elice.iliceworksbe.notification.repository.EventReminderRepository;
import com.elice.iliceworksbe.notification.service.EventReminderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventReminderServiceImpl implements EventReminderService {

    private final EventRepository eventRepository;
    private final EventReminderRepository eventReminderRepository;

    /**
     * 일정 생성시 EventReminder 테이블에 insert
     * @param requestDto
     * @return
     */
    @Override
    public EventReminderResponseDto postEventNotification(EventReminderRequestDto requestDto) {
        Event event = eventRepository.findById(requestDto.eventId())
                .orElseThrow(() -> new BaseException(ErrorCode.EVENT_NOT_FOUND));

        EventReminder eventReminder = EventReminder.from(requestDto);
        eventReminder.assignEvent(event);

        EventReminder savedEventReminder = eventReminderRepository.save(eventReminder);
        return EventReminderResponseDto.from(savedEventReminder);
    }

}
