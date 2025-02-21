package com.elice.iliceworksbe.team.utils;

import java.security.SecureRandom;
import java.util.Random;

public class PasswordGenerator {
    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL_CHARACTERS = "!@#$%^&*()-_=+<>?";
    private static final String ALL_CHARACTERS = LOWERCASE + UPPERCASE + DIGITS + SPECIAL_CHARACTERS;
    private static final int MIN_LENGTH = 8;
    private static final int MAX_LENGTH = 16;

    private static final Random RANDOM = new SecureRandom();

    public static String generatePassword() {
        int length = RANDOM.nextInt((MAX_LENGTH - MIN_LENGTH) + 1) + MIN_LENGTH; // 8~16 길이 랜덤 선택

        StringBuilder password = new StringBuilder();

        password.append(getRandomChar(LOWERCASE));
        password.append(getRandomChar(UPPERCASE));
        password.append(getRandomChar(DIGITS));
        password.append(getRandomChar(SPECIAL_CHARACTERS));

        for (int i = 4; i < length; i++) {
            password.append(getRandomChar(ALL_CHARACTERS));
        }

        return shuffleString(password.toString());
    }

    private static char getRandomChar(String characters) {
        return characters.charAt(RANDOM.nextInt(characters.length()));
    }

    private static String shuffleString(String input) {
        char[] characters = input.toCharArray();
        for (int i = characters.length - 1; i > 0; i--) {
            int j = RANDOM.nextInt(i + 1);
            char temp = characters[i];
            characters[i] = characters[j];
            characters[j] = temp;
        }
        return new String(characters);
    }
}
