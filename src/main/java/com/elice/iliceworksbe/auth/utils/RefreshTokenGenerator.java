package com.elice.iliceworksbe.auth.utils;

import java.security.SecureRandom;
import java.util.Base64;

public class RefreshTokenGenerator {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final int TOKEN_BYTE_LENGTH = 32; // 256-bit 토큰

    /**
     * Refresh Token 생성
     * @return URL-safe Refresh Token
     */
    public static String generateRefreshToken() {
        byte[] randomBytes = new byte[TOKEN_BYTE_LENGTH];
        SECURE_RANDOM.nextBytes(randomBytes); // 랜덤 바이트 생성
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes); // URL-safe Base64
    }
}