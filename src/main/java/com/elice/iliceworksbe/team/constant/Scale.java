package com.elice.iliceworksbe.team.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Scale {
    ONE("1"),
    TWO_TO_NINE("2~9"),
    TEN_TO_NINETEEN("10~19"),
    TWENTY_TO_FORTY_NINE("20~49"),
    FIFTY_TO_NINETY_NINE("50~99"),
    HUNDRED_TO_TWO_NINETY_NINE("100~299"),
    THREE_HUNDRED_OR_MORE("300명 이상");

    private final String koreanName;
}