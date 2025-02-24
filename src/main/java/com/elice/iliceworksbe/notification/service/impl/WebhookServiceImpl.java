package com.elice.iliceworksbe.notification.service.impl;

import com.elice.iliceworksbe.auth.repository.UserRepository;
import com.elice.iliceworksbe.calendar.entity.Calendar;
import com.elice.iliceworksbe.calendar.repository.CalendarRepository;
import com.elice.iliceworksbe.common.constant.CalendarType;
import com.elice.iliceworksbe.common.exception.BaseException;
import com.elice.iliceworksbe.common.exception.ErrorCode;
import com.elice.iliceworksbe.notification.dto.request.WebhookRequestDto;
import com.elice.iliceworksbe.notification.dto.response.WebhookResponseDto;
import com.elice.iliceworksbe.notification.entity.Webhook;
import com.elice.iliceworksbe.notification.repository.WebhookRepository;
import com.elice.iliceworksbe.notification.service.WebhookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class WebhookServiceImpl implements WebhookService {
    private final WebhookRepository webhookRepository;
    private final CalendarRepository calendarRepository;
    private final UserRepository userRepository;

    /**
     * 웹훅 등록
     * @param userId
     * @param requestDto
     * @return
     */
    @Override
    public WebhookResponseDto postWebhook(Long userId, WebhookRequestDto requestDto) {

        // 1. calendar 조회
        Calendar calendar = calendarRepository.findById(requestDto.calendarId())
                .orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND_CALENDAR));
        log.info("calendar의 typeId={}", calendar.getTypeId());

        // 2. user의 team 조회
        Long teamId = userRepository.findTeamIdByUserId(userId)
                .orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND_TEAM));
        log.info("user의 teamId={}", teamId);

        // 3. calendar의 type이 TEAM, typeId가 user가 속한 teamId인지 확인
        if (!(calendar.getType().equals(CalendarType.TEAM) && calendar.getTypeId().equals(teamId))) {
            throw new BaseException(ErrorCode.DENIED_ROLE_PERMISSION);
        }

        Webhook webhook = Webhook.from(requestDto);
        webhook.assignCalendar(calendar);

        Webhook savedWebhook = webhookRepository.save(webhook);
        return WebhookResponseDto.from(savedWebhook);
    }
}
