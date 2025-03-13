package com.elice.iliceworksbe.auth.utils;

import java.security.SecureRandom;

public class VerificationCodeGenerator {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"; // 사용할 문자와 숫자
    private static final int CODE_LENGTH = 6; // 인증 코드 길이
    private static final SecureRandom RANDOM = new SecureRandom(); // SecureRandom 인스턴스

    /**
     * 6자리 인증 코드 생성
     * @return 인증 코드
     */
    public static String generateVerificationCode() {
        StringBuilder verificationCode = new StringBuilder(CODE_LENGTH);

        for (int i = 0; i < CODE_LENGTH; i++) {
            int index = RANDOM.nextInt(CHARACTERS.length());
            verificationCode.append(CHARACTERS.charAt(index));
        }

        return verificationCode.toString();
    }
}
