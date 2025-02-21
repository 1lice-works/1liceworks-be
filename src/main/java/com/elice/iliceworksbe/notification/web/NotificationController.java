package com.elice.iliceworksbe.notification.web;

import com.elice.iliceworksbe.auth.model.UserDetailsImpl;
import com.elice.iliceworksbe.common.exception.BaseResponse;
import com.elice.iliceworksbe.notification.dto.request.WebhookRequestDto;
import com.elice.iliceworksbe.notification.dto.response.NotificationResponseDto;
import com.elice.iliceworksbe.notification.dto.response.WebhookResponseDto;
import com.elice.iliceworksbe.notification.service.NotificationService;
import com.elice.iliceworksbe.notification.service.WebhookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/notification")
public class NotificationController {
    private final NotificationService notificationService;
    private final WebhookService webhookService;

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

    /**
     * 웹훅 등록
     * @param userDetails
     * @param requestDto
     * @return
     */
    @PreAuthorize("hasAuthority('LEADER')")
    @PostMapping("/webhook")
    public BaseResponse<WebhookResponseDto> postWebhook(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody WebhookRequestDto requestDto) {
        Long userId = userDetails.getUserId();
        WebhookResponseDto postResponseDto = webhookService.postWebhook(userId, requestDto);
        return new BaseResponse<>(postResponseDto);
    }
}