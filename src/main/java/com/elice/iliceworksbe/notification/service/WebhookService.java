package com.elice.iliceworksbe.notification.service;

import com.elice.iliceworksbe.notification.dto.request.WebhookRequestDto;
import com.elice.iliceworksbe.notification.dto.response.WebhookResponseDto;

public interface WebhookService {
    WebhookResponseDto postWebhook(Long userId, WebhookRequestDto requestDto);
}
