package com.elice.iliceworksbe.team.dto.position;

import jakarta.validation.constraints.NotBlank;

public record PositionUpdateDto(
        @NotBlank(message = "직급을 입력하세요.")
        String name) {}
