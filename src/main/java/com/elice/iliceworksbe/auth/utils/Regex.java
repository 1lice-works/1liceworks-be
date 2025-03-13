package com.elice.iliceworksbe.auth.utils;

public class Regex {
    /**
     * 비밀번호 정규식
     * 글자수 : 8 ~ 16
     * 조합 : 영문 + 숫자 + 특수문자 -> 필수
     * 특수문자 : @, !만 허용
     * 대소문자 제한 x
     */
    public static final String PASSWORD = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@!])[a-zA-Z\\d@!]{8,16}$";
    public static final String NAME = "^[가-힣a-zA-Z]{1,10}$"; // 10글자 이하, 한글/영문 허용
}

