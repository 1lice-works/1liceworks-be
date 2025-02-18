package com.elice.iliceworksbe.notification.web;


import com.elice.iliceworksbe.calendar.entity.Event;
import com.elice.iliceworksbe.calendar.repository.EventRepository;
import com.elice.iliceworksbe.common.exception.BaseException;
import com.elice.iliceworksbe.common.exception.ErrorCode;
import com.elice.iliceworksbe.notification.dto.request.EventNotificationRequestDto;
import com.elice.iliceworksbe.notification.dto.response.EventNotificationResponseDto;
import com.elice.iliceworksbe.notification.service.impl.NotificationServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/notification")
public class NotificationController {
    private final NotificationServiceImpl notificationService;

    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@AuthenticationPrincipal UserDetails userDetails) {
        // 인증되지 않은 사용자 예외 처리
        if (userDetails == null || userDetails.getUsername() == null || userDetails.getUsername().isEmpty()) {
            log.warn("SSE 구독 요청 거부: 인증되지 않은 사용자");
            throw new BaseException(ErrorCode.INVALID_USER_JWT);
        }
        String username = userDetails.getUsername();
        log.info("SSE 구독 요청: username={}", username);
        return notificationService.createEmitter(username); // SseEmitter를 반환
    }

    @PostMapping("test")
    public ResponseEntity<EventNotificationResponseDto> createEventNotification(
            @RequestBody EventNotificationRequestDto requestDto) {

        log.info("test");
        // 알림 생성
        EventNotificationResponseDto response = notificationService.createEventNotification(requestDto);
        log.info("test");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}