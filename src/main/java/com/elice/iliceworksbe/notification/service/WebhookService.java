package com.elice.iliceworksbe.notification.service;

import com.elice.iliceworksbe.notification.dto.request.WebhookMessageDto;
import com.elice.iliceworksbe.notification.dto.request.WebhookRequestDto;
import com.elice.iliceworksbe.notification.dto.request.WebhookUpdateDto;
import com.elice.iliceworksbe.notification.dto.response.WebhookResponseDto;

public interface WebhookService {
    WebhookResponseDto postWebhook(Long userId, WebhookRequestDto requestDto);
    boolean sendWebhookMessage(Long calendarId, WebhookMessageDto webhookMessageDto);
    WebhookResponseDto getWebhook(Long webhookId);
    WebhookResponseDto patchWebhook(Long webhookId, WebhookUpdateDto webhookUpdateDto);
    void deleteWebhook(Long webhookId);
}
