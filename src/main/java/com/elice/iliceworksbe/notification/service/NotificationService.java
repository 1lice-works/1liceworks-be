package com.elice.iliceworksbe.notification.service;

import com.elice.iliceworksbe.notification.dto.request.NotificationRequestDto;
import com.elice.iliceworksbe.notification.dto.response.NotificationResponseDto;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;


public interface NotificationService {
    SseEmitter createEmitter(Long userId);
    void sendNotification(NotificationRequestDto notificationRequestDto);
    List<NotificationResponseDto> getNotifications(Long userId);
}
