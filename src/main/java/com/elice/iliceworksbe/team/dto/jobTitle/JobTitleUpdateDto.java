package com.elice.iliceworksbe.team.dto.jobTitle;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record JobTitleUpdateDto(
        @NotBlank(message = "직책을 입력하세요.")
        @Size(max = 20, message = "변경할 직책명은 최대 20자까지 가능합니다.")
        String name) {}
