package com.elice.iliceworksbe.auth.dto.request;

public record ChangePasswordRequestDto(
        String privateEmail,
        String accountId,
        String newPassword
) {
}
