package com.elice.iliceworksbe.notification.dto.request;

import lombok.Builder;

@Builder
public record NotificationRequestDto(Long userId, String message) {
}
