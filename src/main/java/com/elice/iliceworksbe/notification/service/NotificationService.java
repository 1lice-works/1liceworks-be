package com.elice.iliceworksbe.notification.service;

import com.elice.iliceworksbe.notification.dto.request.NotificationRequestDto;
import com.elice.iliceworksbe.notification.dto.response.NotificationResponseDto;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;


public interface NotificationService {
    SseEmitter createEmitter(Long userId);
    void disconnect(Long userId);
    void sendNotification(NotificationRequestDto notificationRequestDto);
    void updateNotificationStatus(Long notificationId, boolean isSent);
    NotificationResponseDto postNotification(NotificationRequestDto requestDto);
    List<NotificationResponseDto> getNotifications(Long userId);
}
