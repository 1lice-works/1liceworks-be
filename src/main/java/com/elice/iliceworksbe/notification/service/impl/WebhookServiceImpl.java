package com.elice.iliceworksbe.notification.service.impl;

import com.elice.iliceworksbe.auth.repository.UserRepository;
import com.elice.iliceworksbe.calendar.entity.Calendar;
import com.elice.iliceworksbe.calendar.repository.CalendarRepository;
import com.elice.iliceworksbe.common.constant.CalendarType;
import com.elice.iliceworksbe.common.exception.BaseException;
import com.elice.iliceworksbe.common.exception.ErrorCode;
import com.elice.iliceworksbe.notification.dto.request.WebhookMessageDto;
import com.elice.iliceworksbe.notification.dto.request.WebhookRequestDto;
import com.elice.iliceworksbe.notification.dto.request.WebhookUpdateDto;
import com.elice.iliceworksbe.notification.dto.response.WebhookResponseDto;
import com.elice.iliceworksbe.notification.entity.Webhook;
import com.elice.iliceworksbe.notification.repository.WebhookRepository;
import com.elice.iliceworksbe.notification.service.WebhookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


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
    @Transactional
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
            throw new BaseException(ErrorCode.INVALID_USER_JWT);
        }

        Webhook webhook = Webhook.from(requestDto);
        webhook.assignCalendar(calendar);

        Webhook savedWebhook = webhookRepository.save(webhook);
        return WebhookResponseDto.from(savedWebhook);
    }

    /**
     * 웹훅 전송
     * @param calendarId
     * @param webhookMessageDto
     * @return
     */
    @Transactional
    @Override
    public boolean sendWebhookMessage(Long calendarId, WebhookMessageDto webhookMessageDto) {

        //calendarId로 webhook 조회
        Webhook webhook = webhookRepository.findByCalendarId(calendarId)
                .orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND_WEBHOOK));

        String payloadUrl = webhook.getPayloadUrl();
        String contentType = webhook.getContentType().getValue();

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) { // HttpClient 생성
            HttpPost httpPost = new HttpPost(payloadUrl);
            httpPost.setHeader("Content-Type", contentType + "; charset=UTF-8");

            ObjectMapper objectMapper = new ObjectMapper();

            // WebhookMessageDto를 JSON으로 변환
            String jsonPayload = objectMapper.writeValueAsString(webhookMessageDto);
            httpPost.setEntity(new StringEntity(jsonPayload, "UTF-8"));

            // HTTP 요청 실행
            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                int statusCode = response.getStatusLine().getStatusCode();

                // response에 대한 처리
                if (statusCode != HttpStatus.NO_CONTENT.value()) {
                    log.error("메시지 전송 실패, 응답 코드: {}", statusCode);
                    log.error("응답 내용: {}", EntityUtils.toString(response.getEntity()));
                    return false;
                }
            }
        } catch (Exception e) {
            log.error("에러 발생: {}", e.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public WebhookResponseDto getWebhook(Long webhookId) {
        Webhook findedWebhook = webhookRepository.findById(webhookId)
                .orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND_WEBHOOK));
        return WebhookResponseDto.from(findedWebhook);
    }

    @Transactional
    @Override
    public WebhookResponseDto patchWebhook(Long webhookId, WebhookUpdateDto webhookUpdateDto) {
        Webhook findedWebhook = webhookRepository.findById(webhookId)
                .orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND_WEBHOOK));
        findedWebhook.update(webhookUpdateDto);

        Webhook updatedWebhook = webhookRepository.save(findedWebhook);
        return WebhookResponseDto.from(updatedWebhook);
    }

    @Transactional
    @Override
    public void deleteWebhook(Long webhookId) {
        webhookRepository.deleteById(webhookId);
    }
}
