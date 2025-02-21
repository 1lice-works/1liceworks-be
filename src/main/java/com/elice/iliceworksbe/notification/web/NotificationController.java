package com.elice.iliceworksbe.notification.web;

import com.elice.iliceworksbe.auth.model.UserDetailsImpl;
import com.elice.iliceworksbe.common.exception.BaseException;
import com.elice.iliceworksbe.common.exception.BaseResponse;
import com.elice.iliceworksbe.common.exception.ErrorCode;
import com.elice.iliceworksbe.notification.dto.response.NotificationResponseDto;
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
        Long userId = userDetails.getUserId();
        log.info("SSE 구독 요청: userId={}", userId);
        return notificationService.createEmitter(userId); // SseEmitter를 반환
    }

    @GetMapping
    public BaseResponse<List<NotificationResponseDto>> getNotifications(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<NotificationResponseDto> getResponseDtos = notificationService.getNotifications(userDetails.getUserId());
        return new BaseResponse<>(getResponseDtos);
    }

}