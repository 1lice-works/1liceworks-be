package com.elice.iliceworksbe.notification.service;

import com.elice.iliceworksbe.notification.dto.request.EventNotificationRequestDto;
import com.elice.iliceworksbe.notification.dto.response.EventNotificationResponseDto;
import com.elice.iliceworksbe.notification.entity.EventNotification;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface NotificationService {
    SseEmitter createEmitter(String username);
//    void checkAndSendScheduledNotification();

//    void sendScheduledNotification(EventNotification notification);

    EventNotificationResponseDto createEventNotification(EventNotificationRequestDto eventNotificationRequestDto);
}