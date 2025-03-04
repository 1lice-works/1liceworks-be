package com.elice.iliceworksbe.notification.service;

import com.elice.iliceworksbe.auth.entity.User;
import com.elice.iliceworksbe.auth.repository.UserRepository;
import com.elice.iliceworksbe.common.exception.BaseException;
import com.elice.iliceworksbe.common.exception.ErrorCode;
import com.elice.iliceworksbe.notification.dto.request.NotificationRequestDto;
import com.elice.iliceworksbe.notification.dto.response.NotificationResponseDto;
import com.elice.iliceworksbe.notification.entity.Notification;
import com.elice.iliceworksbe.notification.repository.NotificationRepository;
import com.elice.iliceworksbe.notification.service.impl.NotificationServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@Slf4j
@Transactional
@SpringBootTest
@ActiveProfiles("test")
class NotificationServiceTest {
    @InjectMocks
    private NotificationServiceImpl notificationService;

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private UserRepository userRepository;
    @Mock
    private Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    @Mock
    private SseEmitter mockEmitter;

    @Mock
    private NotificationService notificationServiceSpy; // 부분 Mocking을 위한 Spy

    @BeforeEach
    void setUp() {
        notificationServiceSpy = Mockito.spy(notificationService);
    }

    @Test
    @DisplayName("SSE 연결 생성 성공 - 새로운 Emitter")
    void createEmitter_Success_NewEmitter() {
        // given
        Long userId = 1L;

        // when
        SseEmitter emitter = notificationService.createEmitter(userId);
        log.info("emitter={}", emitter.toString());

        // then
        assertNotNull(emitter); // 반환된 Emitter가 null이 아닌지 확인
        verify(notificationRepository, times(1)).findUnsentNotifications(userId); // 미전송 알림 조회 확인
        verify(notificationRepository, times(1)).existsByUserIdAndIsReadFalse(userId); // 읽지 않은 알림 여부 조회 확인
    }

    @Test
    @DisplayName("알림 전송 성공 - SSE 미구독")
    void sendNotification_Success_WithoutSSE() {
        // given
        Long userId = 1L;
        String message = "test notification";

        User mockUser = User.builder()
                .id(userId)
                .accountId("test@domain.com")
                .username("Test User")
                .build();

        NotificationRequestDto requestDto = new NotificationRequestDto(userId, message);
        NotificationResponseDto mockResponse = new NotificationResponseDto(1L, message, LocalDateTime.now());

        given(userRepository.findById(userId)).willReturn(Optional.of(mockUser));
        doReturn(mockResponse).when(notificationServiceSpy).postNotification(any(NotificationRequestDto.class));

        given(emitters.get(userId)).willReturn(null); // SSE 미구독 상태

        // when
        notificationServiceSpy.sendNotification(requestDto);

        // then
        verify(notificationServiceSpy).postNotification(requestDto);
        verify(emitters, never()).get(userId);
        verify(notificationServiceSpy, never()).updateNotificationStatus(anyLong(), eq(true));
    }

    @Test
    @DisplayName("알림 전송 실패 - 존재하지 않는 사용자")
    void sendNotification_Fail_UserNotFound() {
        // given
        Long invalidUserId = -1L;
        String message = "test notification";
        NotificationRequestDto requestDto = new NotificationRequestDto(invalidUserId, message);

        given(userRepository.findById(invalidUserId)).willReturn(Optional.empty());

        // when & then
        BaseException exception = assertThrows(BaseException.class, () ->
                notificationService.sendNotification(requestDto)
        );

        assertEquals(ErrorCode.NOT_FOUND_USER, exception.getStatus());

        verify(userRepository).findById(invalidUserId);
        verify(notificationServiceSpy, never()).postNotification(any());
        verify(notificationServiceSpy, never()).updateNotificationStatus(anyLong(), anyBoolean());
    }

    @Test
    @DisplayName("알림 저장 성공")
    void postNotification_Success() {
        // given
        Long userId = 1L;
        String message = "test notification";

        User mockUser = User.builder()
                .id(userId)
                .accountId("test@domain.com")
                .username("Test User")
                .build();

        NotificationRequestDto requestDto = new NotificationRequestDto(userId, message);

        Notification mockNotification = Notification.builder()
                .id(1L)
                .message(message)
                .notifyTime(LocalDateTime.now())
                .user(mockUser)
                .build();

        NotificationResponseDto expectedResponse = new NotificationResponseDto(
                mockNotification.getId(),
                mockNotification.getMessage(),
                mockNotification.getNotifyTime()
        );

        given(userRepository.findById(userId)).willReturn(Optional.of(mockUser));
        given(notificationRepository.save(any(Notification.class))).willReturn(mockNotification);

        // when
        NotificationResponseDto actualResponse = notificationService.postNotification(requestDto);

        // then
        assertNotNull(actualResponse);
        assertEquals(expectedResponse.notificationId(), actualResponse.notificationId());
        assertEquals(expectedResponse.message(), actualResponse.message());
        assertEquals(expectedResponse.notifyTime(), actualResponse.notifyTime());

        // verify
        verify(userRepository, times(1)).findById(userId); // findById가 1번 호출되었는지 확인
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    @DisplayName("알림 조회 성공")
    void getNotifications_Success() {
        // Given
        Long userId = 1L;

        User mockUser = User.builder()
                .id(userId)
                .accountId("test@domain.com")
                .username("Test User")
                .build();

        // Notification 데이터 생성 (5개)
        List<Notification> notifications = IntStream.range(0, 5)
                .mapToObj(i -> Notification.builder()
                        .id((long) i + 1)
                        .message("테스트 알림 " + i)
                        .notifyTime(LocalDateTime.now().minusDays(i))
                        .isRead(false) // 읽지 않은 상태
                        .isSent(true) // 발송된 상태
                        .user(mockUser)
                        .build())
                .collect(Collectors.toList());

        // Mock 설정
        when(notificationRepository.findTop50ByUserIdAndCreatedAtAfterOrderByCreatedAtDesc(eq(userId), any(LocalDateTime.class)))
                .thenReturn(notifications);

        // When
        List<NotificationResponseDto> result = notificationService.getNotifications(userId);

        // Then
        assertNotNull(result);
        assertEquals(5, result.size()); // 조회된 알림 개수 확인
        assertTrue(result.stream().allMatch(dto -> dto.message().startsWith("테스트 알림"))); // 데이터 변환 확인

        // 읽음 처리 메서드가 호출되었는지 검증
        verify(notificationRepository, times(1)).markAllAsReadByUserId(userId);

        // 알림 조회 메서드가 파라미터로 호출되었는지 검증
        verify(notificationRepository, times(1)).findTop50ByUserIdAndCreatedAtAfterOrderByCreatedAtDesc(eq(userId), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("알림 조회 성공 - 알림이 없을 때 빈 리스트 반환")
    void getNotifications_Success_NoNotifications() {
        // Given
        Long userId = 1L;

        User mockUser = User.builder()
                .id(userId)
                .accountId("test@domain.com")
                .username("Test User")
                .build();

        when(notificationRepository.findTop50ByUserIdAndCreatedAtAfterOrderByCreatedAtDesc(eq(userId), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList()); // 빈 리스트 반환

        // When
        List<NotificationResponseDto> result = notificationService.getNotifications(userId);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty()); // 결과가 빈 리스트인지 확인
        verify(notificationRepository, times(1)).markAllAsReadByUserId(userId); // 읽음 처리 호출 확인
    }

}