package com.elice.iliceworksbe.calendar.service.impl;

import com.elice.iliceworksbe.auth.repository.UserRepository;
import com.elice.iliceworksbe.calendar.repository.CalendarRepository;
import com.elice.iliceworksbe.calendar.repository.EventParticipantRepository;
import com.elice.iliceworksbe.calendar.repository.EventRepository;
import com.elice.iliceworksbe.notification.repository.EventReminderRepository;
import com.elice.iliceworksbe.notification.service.NotificationService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
class EventServiceImplTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private CalendarRepository calendarRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EventReminderRepository eventReminderRepository;

    @Mock
    private EventParticipantRepository eventParticipantRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private EventServiceImpl eventService;

    @Nested
    class postTeamEvent {

        @Test
        void WhenDefault_Success(){

        }

    }
}