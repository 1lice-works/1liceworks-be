package com.elice.iliceworksbe.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CheckDuplicateAccountIdRequestDto(
        @NotBlank(message = "이메일은 필수 입력 값입니다")
        @Email(message = "이메일 형식으로 되어있어야 합니다")
        @Size(max = 300, message = "이메일은 최대 300글자 입니다")
        String accountId
) {}
