package com.elice.iliceworksbe.notification.service.impl;

import com.elice.iliceworksbe.calendar.entity.Event;
import com.elice.iliceworksbe.calendar.entity.EventParticipant;
import com.elice.iliceworksbe.calendar.repository.EventParticipantRepository;
import com.elice.iliceworksbe.calendar.repository.EventRepository;
import com.elice.iliceworksbe.common.exception.BaseException;
import com.elice.iliceworksbe.common.exception.ErrorCode;
import com.elice.iliceworksbe.notification.dto.request.EventReminderRequestDto;
import com.elice.iliceworksbe.notification.dto.request.NotificationRequestDto;
import com.elice.iliceworksbe.notification.dto.response.EventReminderResponseDto;
import com.elice.iliceworksbe.notification.entity.EventReminder;
import com.elice.iliceworksbe.notification.repository.EventReminderRepository;
import com.elice.iliceworksbe.notification.service.EventReminderService;
import com.elice.iliceworksbe.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventReminderServiceImpl implements EventReminderService {

    private final EventRepository eventRepository;
    private final EventReminderRepository eventReminderRepository;
    private final EventParticipantRepository eventParticipantRepository;
    private final NotificationService notificationService;

    /**
     * 일정 생성시 EventReminder 테이블에 insert
     *
     * @param requestDtos
     * @return
     */
    @Transactional
    @Override
    public List<EventReminderResponseDto> postEventReminder(Long eventId, List<EventReminderRequestDto> requestDtos) {
        Event event = findEventById(eventId);
        return saveEventReminders(requestDtos, event);
    }

    private Event findEventById(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND_EVENT));
        return event;
    }

    private List<EventReminderResponseDto> saveEventReminders(List<EventReminderRequestDto> requestDtos, Event event) {
        List<EventReminderResponseDto> responseDtos = new ArrayList<>();

        for (EventReminderRequestDto requestDto : requestDtos) {
            EventReminder eventReminder = EventReminder.from(requestDto);
            eventReminder.assignEvent(event);

            EventReminder savedEventReminder = eventReminderRepository.save(eventReminder);
            responseDtos.add(EventReminderResponseDto.from(savedEventReminder));
        }
        return responseDtos;
    }

    /**
     * EventReminder 조회
     *
     * @param eventId
     * @return
     */
    @Override
    public List<EventReminderResponseDto> getEventReminder(Long eventId) {
        return eventReminderRepository.findAllByEventId(eventId)
                .stream()
                .map(EventReminderResponseDto::from)
                .collect(Collectors.toList());
    }

    /**
     * 일정에 있는 EventReminder 모두 삭제
     *
     * @param eventId
     */
    @Override
    public void deleteAllEventReminderByEventId(Long eventId) {
        try {
            // 해당 일정의 EventReminder 삭제
            eventReminderRepository.deleteByEventId(eventId);
        } catch (EmptyResultDataAccessException e) {
            log.info("eventReminderRepository.deleteByEventId({}) is empty", eventId);
        }
    }

    /**
     * notifyTime이 현재 시간과 일치하는 알림을 사용자에게 전송
     */
    @Override
    @Scheduled(fixedRate = 60000) // 1분마다 실행
    public void checkEventReminder() {
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        LocalDateTime start = now.minusSeconds(30);
        LocalDateTime end = now.plusSeconds(30);

        //페이징 처리로 한 번에 가져오는 데이터 수 제한
        int pageSize = 100;
        Pageable pageable = PageRequest.of(0, pageSize);
        Page<EventReminder> eventRemindersPage;

        do {
            eventRemindersPage = fetchAndProcessReminders(start, end, pageable);

            pageable = pageable.next(); // 다음 페이지로 이동
        } while (eventRemindersPage.hasNext()); // 다음 데이터가 있을 경우 계속 조회

    }

    /**
     * notifyTime이 start와 end 사이에 있는 EventReminder 조회 후 처리
     */
    @Transactional
    public Page<EventReminder> fetchAndProcessReminders(LocalDateTime start, LocalDateTime end, Pageable pageable) {
        Page<EventReminder> eventRemindersPage = eventReminderRepository.findByNotifyTimeBetween(start, end, pageable);
        List<EventReminder> eventReminders = eventRemindersPage.getContent();

        log.info("발송할 알림 개수: {}", eventReminders.size());
        eventReminders.forEach(this::processEventReminder);
        return eventRemindersPage;
    }

    /**
     * 개별 EventReminder 처리
     *
     * @param eventReminder
     */
    @Transactional
    public void processEventReminder(EventReminder eventReminder) {
        Long eventId = eventReminder.getEvent().getId();
        log.info("eventId", eventId);
        String message = "EventReminder : " + eventReminder.getEvent().getTitle();

        List<EventParticipant> participants = eventParticipantRepository.findByEventId(eventId);
        log.info("이벤트 '{}' 에 대한 참가자 수: {}", message, participants.size());

        participants.forEach(participant -> {
            try {
                log.info("알림 전송 - 사용자: {}", participant.getUser().getId());
                NotificationRequestDto requestDto = new NotificationRequestDto(participant.getUser().getId(), message);
                notificationService.sendNotification(requestDto);
            } catch (Exception e) {
                log.error("알림 전송 실패 - 사용자: {}, 오류: {}", participant.getId(), e.getMessage(), e);
            }
        });

    }

}
