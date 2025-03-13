package com.elice.iliceworksbe.notification.service.impl;


import com.elice.iliceworksbe.auth.entity.User;
import com.elice.iliceworksbe.auth.repository.UserRepository;
import com.elice.iliceworksbe.common.exception.BaseException;
import com.elice.iliceworksbe.common.exception.ErrorCode;
import com.elice.iliceworksbe.notification.dto.request.NotificationRequestDto;
import com.elice.iliceworksbe.notification.dto.response.NotificationResponseDto;
import com.elice.iliceworksbe.notification.entity.Notification;
import com.elice.iliceworksbe.notification.repository.NotificationRepository;
import com.elice.iliceworksbe.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    /**
     * SSE 연결
     *
     * @param userId
     * @return
     */
    @Override
    public SseEmitter createEmitter(Long userId) {

        // 기존 Emitter가 있다면 정리 후 새로운 Emitter 생성
        emitters.compute(userId, (key, existingEmitter) -> {
            if (existingEmitter != null) {
                existingEmitter.complete(); // 기존 연결 강제 종료
            }
            return new SseEmitter(30 * 60 * 1000L); // 30분 유지
        });

        SseEmitter emitter = emitters.get(userId);
        configureEmitter(userId, emitter);
        emitters.put(userId, emitter);

        // 미전송 알림 처리
        sendUnsentNotifications(userId, emitter);

        // 클라이언트 재연결 시 읽지 않은 알림 존재 여부 전송
        sendUnreadNotificationsStatus(userId, emitter);

        return emitter;
    }

    private void configureEmitter(Long userId, SseEmitter emitter) {
        emitter.onCompletion(() -> {
            log.info("SSE 연결 종료: {}", userId);
            emitters.remove(userId);
        });

        emitter.onTimeout(() -> {
            log.info("SSE 연결 타임아웃: {}", userId);
            emitters.remove(userId);
        });

        emitter.onError(e -> {
            log.error("SSE 에러 발생 ({}): {}", userId, e.getMessage());
            emitters.remove(userId);
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
     * 미전송 알림 처리
     *
     * @param userId
     * @param emitter
     */
    private void sendUnsentNotifications(Long userId, SseEmitter emitter) {
        List<Notification> unsentNotifications = notificationRepository.findUnsentNotifications(userId);

        if (!unsentNotifications.isEmpty()) {
            log.info("미전송 알림 {}건을 SSE를 통해 전송", unsentNotifications.size());
            unsentNotifications.forEach(notification -> {
                try {
                    emitter.send(SseEmitter.event().name("notification").data(notification.getMessage()));
                    updateNotificationStatus(notification.getId(), true);
                    log.info("{} 전송 성공", notification.getMessage());
                } catch (IOException e) {
                    log.warn("미전송 알림 발송 실패: {}", e.getMessage());
                }
            });
        }
    }

    /**
     * 읽지 않은 알림 존재 여부 전송
     * @param userId
     * @param emitter
     */
    private void sendUnreadNotificationsStatus(Long userId, SseEmitter emitter) {
        boolean hasUnreadNotifications = notificationRepository.existsByUserIdAndIsReadFalse(userId);
        try {
            emitter.send(SseEmitter.event()
                    .name("unreadNotificationStatus")
                    .data(hasUnreadNotifications ? "true" : "false"));
        } catch (IOException e) {
            log.warn("읽지 않은 알림 상태 전송 실패: {}", e.getMessage());
        }
    }

    /**
     * 모든 클라이언트에게 Ping 메시지 전송
     */
    private void sendBroadcastPing() {
        emitters.forEach((userId, emitter) -> {
            try {
                emitter.send(SseEmitter.event().name("ping").data("keep-alive"));
            } catch (IOException | IllegalStateException e) {
                log.warn("Ping 메시지 전송 실패 - 사용자: {}, 이유: {}", userId, e.getMessage());
                emitters.remove(userId);
            }
        });
    }

    /**
     * 1분마다 자동으로 Ping 메시지 전송
     */
    @Scheduled(fixedRate = 60 * 1000L)  // 1분마다 ping 전송
    public void scheduledPing() {
        sendBroadcastPing();
    }

    /**
     * 특정 사용자에게 실시간 알림 전송
     */
    @Override
    @Async("asyncExecutor") // 스레드 풀 사용
    public void sendNotification(NotificationRequestDto requestDto) {
        log.info("[알림 전송 시작] 메시지: {} | 스레드: {}", requestDto.message(), Thread.currentThread().getName());

        // 1. Notification 테이블에 insert
        NotificationResponseDto savedNotification = postNotification(requestDto);
        log.info("저장된 notification = {} {}", savedNotification.notifyTime(), savedNotification.message());

        // 2. SSE 구독 여부 확인
        SseEmitter emitter = emitters.get(requestDto.userId());
        if (emitter != null) {
            try {
                // 3. SSE 실시간 알림 전송
                emitter.send(SseEmitter.event().name("notification").data(requestDto.message()));

                // 4. 전송 성공시 isSent -> true
                updateNotificationStatus(savedNotification.notificationId(), true);
                log.info("SSE 알림 전송 완료: {}", savedNotification.message());

            } catch (IOException | IllegalStateException e) {
                log.warn("실시간 알림 발송 실패: {}", e.getMessage());
                emitters.remove(requestDto.userId(), emitter);
            }
        } else {
            log.info("사용자가 현재 SSE 구독 중이 아님, 로그인 후 알림 확인 가능");
        }
    }

    @Override
    @Transactional
    public void updateNotificationStatus(Long notificationId, boolean isSent) {
        notificationRepository.updateIsSent(notificationId, isSent);
    }

    /**
     * sse 연결 종료
     *
     * @param userId
     */
    @Override
    public void disconnect(Long userId) {
        SseEmitter emitter = emitters.remove(userId);
        if (emitter != null) {
            emitter.complete();  // 연결 종료
        }

    }

    /**
     * Notification 테이블에 insert
     *
     * @param requestDto
     * @return
     */
    @Override
    public NotificationResponseDto postNotification(NotificationRequestDto requestDto) {
        User user = userRepository.findById(requestDto.userId())
                .orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND_USER));

        Notification notification = Notification.from(requestDto);
        notification.assignUser(user);

        Notification savedNotification = notificationRepository.save(notification);
        return NotificationResponseDto.from(savedNotification);
    }

    /**
     * Notification 테이블 조회 (최대 50개, 최대 1달)
     *
     * @param userId
     * @return
     */
    @Override
    @Transactional
    public List<NotificationResponseDto> getNotifications(Long userId) {
        LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);

        //DB에서 isRead = false인 알림 업데이트
        notificationRepository.markAllAsReadByUserId(userId);

        return notificationRepository.findTop50ByUserIdAndCreatedAtAfterOrderByCreatedAtDesc(userId, oneMonthAgo)
                .stream()
                .map(NotificationResponseDto::from)
                .collect(Collectors.toList());
    }

}
