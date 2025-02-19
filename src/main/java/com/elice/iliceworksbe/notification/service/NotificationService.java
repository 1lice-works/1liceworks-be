package com.elice.iliceworksbe.notification.service;

import com.elice.iliceworksbe.notification.dto.request.EventNotificationRequestDto;
import com.elice.iliceworksbe.notification.dto.response.EventNotificationResponseDto;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

public interface NotificationService {
    SseEmitter createEmitter(String username);
    void sendNotification(String username, String message);
    void checkAndSendScheduledNotification();
    EventNotificationResponseDto postEventNotification(EventNotificationRequestDto eventNotificationRequestDto);
    List<EventNotificationResponseDto> getNotification(Long userId);
}
