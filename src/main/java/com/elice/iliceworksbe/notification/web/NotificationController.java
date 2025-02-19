package com.elice.iliceworksbe.notification.web;


import com.elice.iliceworksbe.auth.model.UserDetailsImpl;
import com.elice.iliceworksbe.common.exception.BaseException;
import com.elice.iliceworksbe.common.exception.BaseResponse;
import com.elice.iliceworksbe.common.exception.ErrorCode;
import com.elice.iliceworksbe.notification.dto.response.EventNotificationResponseDto;
import com.elice.iliceworksbe.notification.service.impl.NotificationServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/notification")
public class NotificationController {
    private final NotificationServiceImpl notificationService;

    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        // 인증되지 않은 사용자 예외 처리
        if (userDetails == null || userDetails.getUsername() == null || userDetails.getUsername().isEmpty()) {
            log.warn("SSE 구독 요청 거부: 인증되지 않은 사용자");
            throw new BaseException(ErrorCode.INVALID_USER_JWT);
        }
        String username = userDetails.getUsername();
        log.info("SSE 구독 요청: username={}", username);
        return notificationService.createEmitter(username); // SseEmitter를 반환
    }

    @GetMapping
    public BaseResponse<List<EventNotificationResponseDto>> getAllNotifications(
            @AuthenticationPrincipal UserDetailsImpl userDetails){
        List<EventNotificationResponseDto> getResponseDtos = notificationService.getNotification(userDetails.getUserId());
        return new BaseResponse<>(getResponseDtos);
    }
}