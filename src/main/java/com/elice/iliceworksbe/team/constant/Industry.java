package com.elice.iliceworksbe.team.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Industry {
    CONSTRUCTION("건설/건축"),
    PUBLIC_SECTOR("공공기관"),
    FINANCE_INSURANCE("금융/보험"),
    MARKETING_AD_MEDIA("마케팅/광고/미디어"),
    LEISURE_TRAVEL("레저/여행"),
    RESEARCH_CONSULTING("리서치/컨설팅"),
    LAW("법률"),
    HEALTH_SOCIAL_WELFARE("보건/사회복지"),
    REAL_ESTATE("부동산업"),
    NON_PROFIT("비영리기관"),
    RENTAL_SERVICE("운수 및 임대 서비스"),
    RETAIL("유통/도·소매"),
    FOOD_CAFE("음식/커피 전문점"),
    MEDICAL_PHARMACEUTICAL("의료/제약"),
    IT_TELECOMMUNICATIONS("정보통신/IT"),
    MANUFACTURING("제조"),
    FASHION_BEAUTY("패션/뷰티"),
    EDUCATION("학교 및 교육 서비스"),
    OTHER("기타");

    private final String koreanName;
}