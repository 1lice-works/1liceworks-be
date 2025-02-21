package com.elice.iliceworksbe.notification.dto.request;

import com.elice.iliceworksbe.common.constant.ContentType;

public record WebhookRequestDto(Long calendarId, String payloadUrl, ContentType contentType) {
}
