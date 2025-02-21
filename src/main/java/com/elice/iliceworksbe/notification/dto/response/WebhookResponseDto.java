package com.elice.iliceworksbe.notification.dto.response;

import com.elice.iliceworksbe.common.constant.ContentType;
import com.elice.iliceworksbe.notification.entity.Webhook;

public record WebhookResponseDto(Long webhookId, String payloadUrl, ContentType contentType) {
    public static WebhookResponseDto from(Webhook webhook) {
        return new WebhookResponseDto(webhook.getId(), webhook.getPayloadUrl(), webhook.getContentType());
    }
}
