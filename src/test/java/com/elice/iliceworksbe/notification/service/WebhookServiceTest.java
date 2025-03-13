package com.elice.iliceworksbe.notification.service;

import com.elice.iliceworksbe.auth.entity.User;
import com.elice.iliceworksbe.auth.repository.UserRepository;
import com.elice.iliceworksbe.calendar.entity.Calendar;
import com.elice.iliceworksbe.calendar.repository.CalendarRepository;
import com.elice.iliceworksbe.common.constant.CalendarType;
import com.elice.iliceworksbe.common.constant.ContentType;
import com.elice.iliceworksbe.common.constant.Role;
import com.elice.iliceworksbe.common.constant.Status;
import com.elice.iliceworksbe.common.exception.BaseException;
import com.elice.iliceworksbe.common.exception.ErrorCode;
import com.elice.iliceworksbe.notification.dto.request.WebhookRequestDto;
import com.elice.iliceworksbe.notification.dto.request.WebhookUpdateDto;
import com.elice.iliceworksbe.notification.dto.response.WebhookResponseDto;
import com.elice.iliceworksbe.notification.entity.Webhook;
import com.elice.iliceworksbe.notification.repository.WebhookRepository;
import com.elice.iliceworksbe.notification.service.impl.WebhookServiceImpl;
import com.elice.iliceworksbe.team.constant.Industry;
import com.elice.iliceworksbe.team.constant.Scale;
import com.elice.iliceworksbe.team.entity.Team;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional
@SpringBootTest
@ActiveProfiles("test")
class WebhookServiceTest {
    @InjectMocks
    private WebhookServiceImpl webhookService;

    @Mock
    private CalendarRepository calendarRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private WebhookRepository webhookRepository;

    private Long teamId = 1L;
    private Long calendarId = 1L;
    private Long userId = 1L;
    private Long webhookId = 1L;

    Team iliceTeamBE = Team.builder()
            .id(teamId)
            .companyName("일리스웍스 컴퍼니")
            .teamName("일리스 BE팀")
            .domainName("threadly.ilice-works.com")
            .hasPrivateDomain(true)
            .industry(Industry.CONSTRUCTION)
            .scale(Scale.TEN_TO_NINETEEN)
            .build();

    Calendar calendarBE = Calendar.builder()
            .id(calendarId)
            .name("일리스 BE팀 캘린더")
            .type(CalendarType.TEAM)
            .typeId(iliceTeamBE.getId())
            .team(iliceTeamBE)
            .build();

    User mockUser = User.builder()
            .id(userId)
            .accountId("hi563@threadly.ilice-works.com")
            .username("Test User")
            .privateEmail("hi563@naver.com")
            .role(Role.LEADER)
            .status(Status.ACTIVE)
            .team(iliceTeamBE)
            .isTeamCreated(true)
            .build();

    Webhook iliceTeamBE_webhook = Webhook.builder()
            .id(webhookId)
            .payloadUrl("https://discord.com/api/webhooks/1342348107215798306/bYtBuXjzlkFdsj2saJ03Mo5GvZUCPaGRFB7EsK7no01urQ61OnPMqvZnY2XJkewuRTcn")
            .contentType(ContentType.APPLICATION_JSON)
            .calendar(calendarBE)
            .build();

    WebhookRequestDto requestDto = new WebhookRequestDto(calendarId, "https://discord.com/api/webhooks/1342348107215798306/bYtBuXjzlkFdsj2saJ03Mo5GvZUCPaGRFB7EsK7no01urQ61OnPMqvZnY2XJkewuRTcn", ContentType.APPLICATION_JSON);

    @Test
    @DisplayName("웹훅 저장 성공")
    void testPostWebhook_Success() {

        // given
        given(calendarRepository.findById(calendarId)).willReturn(Optional.of(calendarBE));
        given(userRepository.findTeamIdByUserId(userId)).willReturn(Optional.of(iliceTeamBE.getId()));
        given(webhookRepository.findByCalendarId(calendarId)).willReturn(Optional.empty());
        given(webhookRepository.save(any(Webhook.class))).willReturn(iliceTeamBE_webhook);
        log.info("테스트 코드에서 calendarId: {}", calendarId);
        log.info("서비스 코드에서 requestDto.calendarId(): {}", requestDto.calendarId());

        // when
        WebhookResponseDto response = webhookService.postWebhook(userId, requestDto);

        // then
        assertNotNull(response);
        assertEquals(requestDto.payloadUrl(), response.payloadUrl());
        verify(webhookRepository, times(1)).save(any(Webhook.class));
    }

    @Test
    @DisplayName("웹훅 저장 실패 - 1. 존재하지 않는 캘린더")
    void testPostWebhook_Fail_CalendarNotFound() {

        // given
        given(calendarRepository.findById(calendarId)).willReturn(Optional.empty());

        // when & then
        BaseException thrown = assertThrows(BaseException.class,
                () -> webhookService.postWebhook(userId, requestDto)
        );

        assertEquals(ErrorCode.NOT_FOUND_CALENDAR, thrown.getStatus());
    }

    @Test
    @DisplayName("웹훅 저장 실패 - 2. 사용자의 팀이 존재하지 않을 때")
    void testPostWebhook_Fail_TeamNotFound() {
        // given
        given(calendarRepository.findById(calendarId)).willReturn(Optional.of(calendarBE));
        given(userRepository.findTeamIdByUserId(userId)).willReturn(Optional.empty());

        // when & then
        BaseException thrown = assertThrows(BaseException.class,
                () -> webhookService.postWebhook(userId, requestDto)
        );

        assertEquals(ErrorCode.NOT_FOUND_TEAM, thrown.getStatus());
    }

    @Test
    @DisplayName("웹훅 저장 실패 - 3. member가 웹훅을 등록하려 할 때")
    void testPostWebhook_Fail_InvalidAuthorization() {
        // given
        User memberUser = User.builder()
                .id(2L)
                .accountId("member@threadly.ilice-works.com")
                .username("member User")
                .privateEmail("member@naver.com")
                .role(Role.MEMBER)
                .status(Status.ACTIVE)
                .team(iliceTeamBE)
                .isTeamCreated(true)
                .build();

        Calendar memberCalendar = Calendar.builder()
                .id(2L)
                .name("member1 / 직급1")
                .type(CalendarType.MEMBER)
                .typeId(memberUser.getId())
                .team(iliceTeamBE)
                .build();
        WebhookRequestDto requestDto = new WebhookRequestDto(memberCalendar.getId(), "https://discord.com/api/webhooks/1342348107215798306/bYtBuXjzlkFdsj2saJ03Mo5GvZUCPaGRFB7EsK7no01urQ61OnPMqvZnY2XJkewuRTcn", ContentType.APPLICATION_JSON);

        given(calendarRepository.findById(memberCalendar.getId())).willReturn(Optional.of(memberCalendar));
        given(userRepository.findTeamIdByUserId(memberUser.getId())).willReturn(Optional.of(teamId));

        // when & then
        BaseException thrown = assertThrows(BaseException.class,
                () -> webhookService.postWebhook(memberUser.getId(), requestDto)
        );

        assertEquals(ErrorCode.INVALID_AUTHORIZATION, thrown.getStatus());
    }

    @Test
    @DisplayName("웹훅 저장 실패 - 이미 존재하는 웹훅")
    void testPostWebhook_DuplicatedWebhook() {
        // Given
        given(calendarRepository.findById(calendarId)).willReturn(Optional.of(calendarBE));
        given(userRepository.findTeamIdByUserId(userId)).willReturn(Optional.of(teamId));
        given(webhookRepository.findByCalendarId(calendarId)).willReturn(Optional.of(iliceTeamBE_webhook));
        WebhookRequestDto requestDto = new WebhookRequestDto(calendarId, "https://discord.com/api/webhooks/1342348107215798306/bYtBuXjzlkFdsj2saJ03Mo5GvZUCPaGRFB7EsK7no01urQ61OnPMqvZnY2XJkewuRTcn", ContentType.APPLICATION_JSON);

        // When & Then
        BaseException thrown = assertThrows(BaseException.class,
                () -> webhookService.postWebhook(userId, requestDto)
        );
        log.info("thrown={}", thrown.getStatus());

        assertEquals(ErrorCode.DUPLICATED_WEBHOOK, thrown.getStatus());
    }

    @Test
    @DisplayName("웹훅 조회 성공")
    void getWebhook_Success() {
        // given
        WebhookResponseDto expectedResponseDto = WebhookResponseDto.from(iliceTeamBE_webhook);
        given(webhookRepository.findById(webhookId)).willReturn(Optional.of(iliceTeamBE_webhook));

        // When
        WebhookResponseDto result = webhookService.getWebhook(webhookId);

        // Then
        verify(webhookRepository, times(1)).findById(webhookId);
        assertEquals(expectedResponseDto, result);
    }

    @Test
    @DisplayName("웹훅 조회 실패 - 저장된 웹훅이 없는 경우")
    void getWebhook_Fail_NotFound() {
        // given
        given(webhookRepository.findById(webhookId)).willReturn(Optional.empty());

        // when / then
        BaseException thrown = assertThrows(BaseException.class, () -> {
            webhookService.getWebhook(webhookId);
        });

        // 예외 코드가 맞는지 확인
        assertEquals(ErrorCode.NOT_FOUND_WEBHOOK, thrown.getStatus());
    }

    @Test
    @DisplayName("웹훅 수정 성공")
    void patchWebhook_Success() {
        // Given
        WebhookUpdateDto webhookUpdateDto = new WebhookUpdateDto("https://new-url.com", ContentType.APPLICATION_JSON);
        Webhook updatedWebhook = Webhook.builder()
                .id(webhookId)
                .payloadUrl("https://new-url.com")
                .contentType(ContentType.APPLICATION_JSON)
                .build();

        given(webhookRepository.findById(webhookId)).willReturn(Optional.of(iliceTeamBE_webhook));
        given(webhookRepository.save(any(Webhook.class))).willReturn(updatedWebhook); // any()를 사용하여 save() 메서드 호출을 모의

        // When
        WebhookResponseDto result = webhookService.patchWebhook(webhookId, webhookUpdateDto);

        // Then
        verify(webhookRepository, times(1)).findById(webhookId); // findById가 호출되었는지 확인
        verify(webhookRepository, times(1)).save(any(Webhook.class)); // save 호출 확인
        assertEquals("https://new-url.com", result.payloadUrl()); // 업데이트된 URL 확인
        assertEquals(ContentType.APPLICATION_JSON, result.contentType()); // 업데이트된 ContentType 확인
    }

    @Test
    @DisplayName("웹훅 수정 실패 - 저장된 웹훅이 없을 경우")
    void patchWebhook_Fail_NotFound() {
        // given
        WebhookUpdateDto webhookUpdateDto = new WebhookUpdateDto("https://new-url.com", ContentType.APPLICATION_JSON);
        given(webhookRepository.findById(webhookId)).willReturn(Optional.empty());

        // when / then
        BaseException thrown = assertThrows(BaseException.class, () -> {
            webhookService.patchWebhook(webhookId, webhookUpdateDto);
        });

        // 예외 코드가 맞는지 확인
        assertEquals(ErrorCode.NOT_FOUND_WEBHOOK, thrown.getStatus());
    }

    @Test
    @DisplayName("웹훅 삭제 성공")
    void deleteWebhook_Success() {
        // given
        given(webhookRepository.findById(webhookId)).willReturn(Optional.of(iliceTeamBE_webhook));

        // when
        webhookService.deleteWebhook(webhookId); // 삭제 메서드 호출

        // then
        verify(webhookRepository, times(1)).deleteById(webhookId);

        // 삭제 후 해당 webhookId로 조회 시, 예외가 발생하는지 확인
        given(webhookRepository.findById(webhookId)).willReturn(Optional.empty()); // 삭제 후 조회 결과는 empty
        assertThrows(BaseException.class, () -> webhookService.deleteWebhook(webhookId)); // 예외가 발생해야 함
    }

    @Test
    @DisplayName("웹훅 삭제 실패 - 웹훅이 존재하지 않음")
    void deleteWebhook_Fail_NotFound() {
        // given
        given(webhookRepository.findById(webhookId)).willReturn(Optional.empty());

        // when & then
        // 삭제 메서드를 호출할 때 예외가 발생
        assertThrows(BaseException.class, () -> webhookService.deleteWebhook(webhookId));

        // deleteById는 호출되지 않아야 한다
        verify(webhookRepository, times(0)).deleteById(webhookId);
    }

    @Test
    void sendWebhookMessage() {
    }

}