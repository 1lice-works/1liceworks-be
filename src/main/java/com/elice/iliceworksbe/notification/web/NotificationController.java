package com.elice.iliceworksbe.notification.web;

import com.elice.iliceworksbe.auth.model.UserDetailsImpl;
import com.elice.iliceworksbe.common.exception.BaseResponse;
import com.elice.iliceworksbe.common.exception.ErrorCode;
import com.elice.iliceworksbe.notification.dto.request.WebhookRequestDto;
import com.elice.iliceworksbe.notification.dto.request.WebhookUpdateDto;
import com.elice.iliceworksbe.notification.dto.response.NotificationResponseDto;
import com.elice.iliceworksbe.notification.dto.response.WebhookResponseDto;
import com.elice.iliceworksbe.notification.service.NotificationService;
import com.elice.iliceworksbe.notification.service.WebhookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
@Tag(name = "Notification", description = "알림 관련 API 입니다.")
public class NotificationController {
    private final NotificationService notificationService;
    private final WebhookService webhookService;

    @Operation(summary = "SSE 구독 요청", description = "SSE 구독을 요청합니다.")
    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = userDetails.getUserId();
        log.info("SSE 구독 요청: userId={}", userId);
        return notificationService.createEmitter(userId); // SseEmitter를 반환
    }

    @Operation(summary = "알림 조회", description = "알림을 조회합니다.")
    @GetMapping
    public BaseResponse<List<NotificationResponseDto>> getNotifications(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<NotificationResponseDto> getResponseDtos = notificationService.getNotifications(userDetails.getUserId());
        return new BaseResponse<>(getResponseDtos);
    }

    @Operation(summary = "웹훅 등록", description = "웹훅을 등록합니다.")
    @PreAuthorize("hasAuthority('LEADER')")
    @PostMapping("/webhook")
    public BaseResponse<WebhookResponseDto> postWebhook(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody WebhookRequestDto requestDto) {
        Long userId = userDetails.getUserId();
        WebhookResponseDto postResponseDto = webhookService.postWebhook(userId, requestDto);
        return new BaseResponse<>(postResponseDto);
    }

    @Operation(summary = "웹훅 조회", description = "웹훅을 조회합니다.")
    @PreAuthorize("hasAuthority('LEADER')")
    @GetMapping("/webhook/{webhookId}")
    public BaseResponse<WebhookResponseDto> getWebhook(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long webhookId) {
        WebhookResponseDto getResponseDto = webhookService.getWebhook(webhookId);
        return new BaseResponse<>(getResponseDto);
    }

    @Operation(summary = "웹훅 수정", description = "웹훅을 수정합니다.")
    @PreAuthorize("hasAuthority('LEADER')")
    @PatchMapping("/webhook/{webhookId}")
    public BaseResponse<WebhookResponseDto> patchWebhook(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long webhookId,
            @Valid @RequestBody WebhookUpdateDto webhookUpdateDto) {
        WebhookResponseDto patchResponseDto = webhookService.patchWebhook(webhookId, webhookUpdateDto);
        return new BaseResponse<>(patchResponseDto);
    }

    @Operation(summary = "웹훅 삭제", description = "웹훅을 삭제합니다.")
    @PreAuthorize("hasAuthority('LEADER')")
    @DeleteMapping("/webhook/{webhookId}")
    public BaseResponse<String> deleteWebhook(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long webhookId
    ){
        webhookService.deleteWebhook(webhookId);
        return new BaseResponse<>(ErrorCode.NO_CONTENT);
    }

}