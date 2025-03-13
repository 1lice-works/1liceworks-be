package com.elice.iliceworksbe.notification.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationMessages {
    CREATE_TEAM("팀 캘린더에 일정이 생성됐습니다.");

    private final String message;
}
