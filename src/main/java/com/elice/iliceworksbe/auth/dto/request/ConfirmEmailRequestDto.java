package com.elice.iliceworksbe.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ConfirmEmailRequestDto(
        @NotBlank(message = "이메일은 필수 입력 값입니다")
        @Email(message = "이메일 형식으로 되어있어야 합니다")
        @Size(max = 300, message = "이메일은 최대 300글자 입니다")
        String email,

        @NotBlank(message = "인증코드는 필수 입력 값입니다")
        @Size(min = 6, max = 6, message = "인증코드는 정확히 6자리여야합니다")
        String verificationCode
){}
