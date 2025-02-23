package com.elice.iliceworksbe.notification.dto.request;

import com.elice.iliceworksbe.common.constant.ContentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record WebhookRequestDto(
        @NotNull(message = "calenderId는 필수 입력 값입니다")
        Long calendarId,
        @NotBlank(message = "payloadUrl은 필수 입력 값입니다")
        @Pattern(regexp = "^(https?|ftp)://[^\s/$.?#].[^\s]*$", message = "payloadUrl은 유효한 HTTP/HTTPS URL이어야 합니다")
        String payloadUrl,
        @NotNull(message = "contentType은 필수 입력 값입니다")
        ContentType contentType) {
}
