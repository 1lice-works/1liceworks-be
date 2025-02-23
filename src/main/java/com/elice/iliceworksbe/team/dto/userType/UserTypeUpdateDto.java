package com.elice.iliceworksbe.team.dto.userType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserTypeUpdateDto(
        @NotBlank(message = "사용자 유형을 입력하세요.")
        @Size(max = 20, message = "변경할 사용자 유형명은 최대 20자까지 가능합니다.")
        String name) {}
