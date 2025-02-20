package com.elice.iliceworksbe.auth.dto.request;

public record PatchProfileRequestDto(
        String username,
        String phone,
        String responsibility
) {}
