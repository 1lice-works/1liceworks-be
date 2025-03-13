package com.elice.iliceworksbe.auth.dto;

public record LoginRequestDTO(
        String accountId,
        String password
) {}
