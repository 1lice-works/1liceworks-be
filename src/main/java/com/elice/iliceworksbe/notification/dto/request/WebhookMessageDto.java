package com.elice.iliceworksbe.notification.dto.request;

import lombok.Builder;

@Builder
public record WebhookMessageDto(String content) {
}
