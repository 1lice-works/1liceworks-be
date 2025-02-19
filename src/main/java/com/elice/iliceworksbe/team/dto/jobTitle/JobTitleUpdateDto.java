package com.elice.iliceworksbe.team.dto.jobTitle;

import jakarta.validation.constraints.NotBlank;

public record JobTitleUpdateDto(
        @NotBlank(message = "직책을 입력하세요.")
        String name) {}
