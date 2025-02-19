package com.elice.iliceworksbe.notification.service.impl;


import com.elice.iliceworksbe.auth.entity.User;
import com.elice.iliceworksbe.auth.repository.UserRepository;
import com.elice.iliceworksbe.calendar.entity.Event;
import com.elice.iliceworksbe.calendar.repository.EventRepository;
import com.elice.iliceworksbe.common.exception.BaseException;
import com.elice.iliceworksbe.common.exception.ErrorCode;
import com.elice.iliceworksbe.notification.dto.request.EventNotificationRequestDto;
import com.elice.iliceworksbe.notification.dto.response.EventNotificationResponseDto;
import com.elice.iliceworksbe.notification.entity.EventNotification;
import com.elice.iliceworksbe.notification.repository.NotificationRepository;
import com.elice.iliceworksbe.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    /**
     * SSE 연결
     *
     * @param username
     * @return
     */
    @Override
    public SseEmitter createEmitter(String username) {
        SseEmitter emitter = new SseEmitter(60 * 1000L); // 1분 유지
        configureEmitter(username, emitter);
        emitters.put(username, emitter);
        return emitter;
    }

    private void configureEmitter(String username, SseEmitter emitter) {
        emitter.onCompletion(() -> {
            log.info("SSE 연결 종료: {}", username);
            emitters.remove(username);
        });

        emitter.onTimeout(() -> {
            log.info("SSE 연결 타임아웃: {}", username);
            emitters.remove(username);
        });

        emitter.onError(e -> {
            log.error("SSE 에러 발생 ({}): {}", username, e.getMessage());
            emitters.remove(username);
            try {
                //클라이언트에게 error 이벤트 전송
                emitter.send(SseEmitter.event()
                        .name("error")
                        .data("서버 오류가 발생했습니다."));
            } catch (IOException ioException) {
                log.error("클라이언트로 에러 메시지 전송 실패: {}", ioException.getMessage());
            }
            emitter.complete();

        });
    }

    /**
     * 특정 사용자에게 실시간 알림 전송
     *
     * @param username
     * @param message
     */

    @Override
    public void sendNotification(String username, String message) {
        SseEmitter emitter = emitters.get(username);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event().name("notification").data(message));
            } catch (IOException | IllegalStateException e) {
                log.warn("실시간 알림 발송 실패");
                emitters.remove(username, emitter);
            }
        }
    }

    /**
     * notifyTime이 현재 시간과 일치하는 알림을 사용자에게 전송
     */
    @Override
    @Transactional
    @Scheduled(fixedRate = 60000) // 1분마다 실행
    public void checkAndSendScheduledNotification() {
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        LocalDateTime start = now.minusSeconds(1);
        LocalDateTime end = now.plusSeconds(1);

        List<EventNotification> notifications = notificationRepository.findByNotifyTimeBetween(start, end);
        log.info("notification 보내기{}", notifications);
        if (!notifications.isEmpty()) {
            log.info("발송할 알림 개수: {}", notifications.size());
            for (EventNotification notification : notifications) {
                try {
                    sendNotification(notification.getUser().getAccountId(), notification.getMessage());
                } catch (Exception e) {
                    log.error("알림 전송 실패 - 사용자: {}, 오류: {}", notification.getUser().getUsername(), e.getMessage(), e);
                }
            }
        }
    }

    /**
     * 일정 생성시 알림 테이블에 insert
     * @param requestDto
     * @return
     */
    @Override
    public EventNotificationResponseDto postEventNotification(EventNotificationRequestDto requestDto) {
        User user = userRepository.findByAccountId(requestDto.username())
                .orElseThrow(() -> new BaseException(ErrorCode.USERS_INFO_UNKNOWN));

        Event event = eventRepository.findById(requestDto.eventId())
                .orElseThrow(() -> new BaseException(ErrorCode.EVENT_NOT_FOUND));

        EventNotification eventNotification = EventNotification.from(requestDto);
        eventNotification.assignUser(user);
        eventNotification.assignEvent(event);

        EventNotification savedNotification = notificationRepository.save(eventNotification);
        return EventNotificationResponseDto.from(savedNotification);
    }

}
