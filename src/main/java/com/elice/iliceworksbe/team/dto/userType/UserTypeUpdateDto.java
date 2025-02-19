package com.elice.iliceworksbe.team.dto.userType;

import jakarta.validation.constraints.NotBlank;

public record UserTypeUpdateDto(
        @NotBlank(message = "사용자 유형을 입력하세요.")
        String name) {}
