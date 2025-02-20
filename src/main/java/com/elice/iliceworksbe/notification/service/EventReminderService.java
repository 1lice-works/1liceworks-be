package com.elice.iliceworksbe.notification.service;

import com.elice.iliceworksbe.notification.dto.request.EventReminderRequestDto;
import com.elice.iliceworksbe.notification.dto.response.EventReminderResponseDto;

public interface EventReminderService {
    EventReminderResponseDto postEventNotification(EventReminderRequestDto eventNotificationRequestDto);

}
