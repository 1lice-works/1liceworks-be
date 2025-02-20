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
import com.elice.iliceworksbe.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventReminderServiceImpl implements EventReminderService {

    private final EventRepository eventRepository;
    private final EventReminderRepository eventReminderRepository;
    private final NotificationService notificationService;

    /**
     * 일정 생성시 EventReminder 테이블에 insert
     * @param requestDtos
     * @return
     */
    @Override
    public List<EventReminderResponseDto> postEventReminder(Long eventId, List<EventReminderRequestDto> requestDtos) {

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new BaseException(ErrorCode.EVENT_NOT_FOUND));
        List<EventReminderResponseDto> responseDtos = new ArrayList<>();

        for(EventReminderRequestDto requestDto : requestDtos) {
            EventReminder eventReminder = EventReminder.from(requestDto);
            eventReminder.assignEvent(event);

            EventReminder savedEventReminder = eventReminderRepository.save(eventReminder);
            responseDtos.add(EventReminderResponseDto.from(savedEventReminder));
        }
        return responseDtos;
    }

    //    /**
//     * notifyTime이 현재 시간과 일치하는 알림을 사용자에게 전송
//     */
//    @Override
//    @Transactional
//    @Scheduled(fixedRate = 60000) // 1분마다 실행
//    public void checkAndSendScheduledNotification() {
//        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
//        LocalDateTime start = now.minusSeconds(1);
//        LocalDateTime end = now.plusSeconds(1);
//
//        List<EventReminder> eventReminder = eventReminderRepository.findByNotifyTimeBetween(start, end);
//        log.info("eventReminder 보내기{}", eventReminder);
//        if (!eventReminder.isEmpty()) {
//            log.info("발송할 알림 개수: {}", eventReminder.size());
//            for (EventReminder eventReminder : eventReminders) {
//                try {
//                    sendNotification(notification.getUser().getAccountId(), notification.getMessage());
//                } catch (Exception e) {
//                    log.error("알림 전송 실패 - 사용자: {}, 오류: {}", notification.getUser().getUsername(), e.getMessage(), e);
//                }
//            }
//        }
//    }

}
