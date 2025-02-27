package com.elice.iliceworksbe.calendar.service.impl;

import com.elice.iliceworksbe.auth.entity.User;
import com.elice.iliceworksbe.auth.repository.UserRepository;
import com.elice.iliceworksbe.calendar.dto.request.PostMyEventRequestDto;
import com.elice.iliceworksbe.calendar.dto.request.PostTeamEventRequestDto;
import com.elice.iliceworksbe.calendar.dto.response.EventJsonResponseDto;
import com.elice.iliceworksbe.calendar.entity.Calendar;
import com.elice.iliceworksbe.calendar.entity.Event;
import com.elice.iliceworksbe.calendar.entity.EventParticipant;
import com.elice.iliceworksbe.calendar.repository.CalendarRepository;
import com.elice.iliceworksbe.calendar.repository.EventParticipantRepository;
import com.elice.iliceworksbe.calendar.repository.EventRepository;
import com.elice.iliceworksbe.calendar.service.EventService;
import com.elice.iliceworksbe.common.constant.CalendarType;
import com.elice.iliceworksbe.common.exception.BaseException;
import com.elice.iliceworksbe.common.exception.ErrorCode;
import com.elice.iliceworksbe.notification.dto.request.NotificationRequestDto;
import com.elice.iliceworksbe.notification.entity.EventReminder;
import com.elice.iliceworksbe.notification.repository.EventReminderRepository;
import com.elice.iliceworksbe.notification.service.NotificationService;
import com.elice.iliceworksbe.notification.utils.NotificationMessages;
import com.elice.iliceworksbe.team.entity.Team;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final CalendarRepository calendarRepository;
    private final UserRepository userRepository;
    private final EventReminderRepository eventReminderRepository;
    private final EventParticipantRepository eventParticipantRepository;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public void postTeamEvent(Long userId, Long calendarId, PostTeamEventRequestDto postTeamEventRequestDto) {

        // 1. 현재 유저의 팀 조회
        Team team = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND_USER))
                .getTeam();

        // 2. CalendarId가 해당 유저의 팀 캘린더가 맞는지 조회
        Calendar teamCalendar = calendarRepository.findById(calendarId)
                .orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND_CALENDAR));

        if (!(teamCalendar.getId().equals(team.getId()) && teamCalendar.getType().equals(CalendarType.TEAM))) {
            throw new BaseException(ErrorCode.NOT_FOUND_CALENDAR);
        }

        // 3. 해당 팀 캘린더에 대해 일정 생성 및 추가
        Event teamEvent = Event.of(postTeamEventRequestDto, teamCalendar);
        eventRepository.save(teamEvent);

        // 4. 생성된 EventReminder 추가
        List<EventReminder> eventReminders = postTeamEventRequestDto.eventReminders()
                .stream()
                .map(eR -> EventReminder.of(eR, teamEvent))
                .toList();
        eventReminderRepository.saveAll(eventReminders);

        // 5. 일정 참석자 추가
        // 5-1. 일정 인원 조회
        List<Long> participantIds = postTeamEventRequestDto.eventParticipants().stream()
                .map(PostTeamEventRequestDto.EventParticipantDto::userId)
                .toList();

        List<User> users = userRepository.findAllById(participantIds);

        // 5-2. 현재 사용자가 일정 참석자 리스트에 포함되어 있지 않다면 users 리스트에 추가
        if (!participantIds.contains(userId)) {
            User currentUser = userRepository.findById(userId)
                    .orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND_USER));
            users.add(currentUser);
        }

        // 5-2. 일정 인원 토대로 일정 참석자 추가 (일정 참석자가 해당 팀원인지 확인하는 로직추가)
        List<EventParticipant> eventParticipants = users.stream()
                .filter(participant -> participant.checkMyTeam(team))
                .map(participant -> EventParticipant.of(teamEvent, participant)).toList();
        eventParticipantRepository.saveAll(eventParticipants);


        // 6. 해당 팀의 모든 구성원에게 일정이 생성됨을 알림
        // 6-1. 해당 팀의 모든 구성원 조회
        List<User> teamUsers = userRepository.findByTeam(team);

        // 6-2. 해당 팀의 모든 구성원들에게 알림 보냄
        teamUsers.forEach(
                m -> notificationService.sendNotification(
                                NotificationRequestDto.builder()
                                        .userId(m.getId())
                                        .message(NotificationMessages.CREATE_TEAM.getMessage())
                                        .build()
                        )
        );
    }

    @Override
    public void postMyEvent(Long userId, PostMyEventRequestDto postMyEventRequestDto) {

        // 1. 현재 유저의 캘린더 조회
        User currentUser = userRepository.findById(userId).orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND_USER));
        Calendar myCalendar = calendarRepository.findByTypeAndTypeId(CalendarType.MEMBER, userId)
                .orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND_CALENDAR));

        // 2. 해당 유저의 일정 추가
        Event myEvent = Event.of(postMyEventRequestDto, myCalendar);
        eventRepository.save(myEvent);

        // 3. 생성된 EventReminder 추가
        List<EventReminder> eventReminders = postMyEventRequestDto.eventReminders()
                .stream()
                .map(eR -> EventReminder.of(eR, myEvent))
                .toList();
        eventReminderRepository.saveAll(eventReminders);

        // 4. 일정 참석자 추가(자신)
        EventParticipant eventParticipant = EventParticipant.builder()
                .user(currentUser)
                .event(myEvent)
                .build();
        eventParticipantRepository.save(eventParticipant);
    }

    @Override
    public List<EventJsonResponseDto> getEventsByDateAndParticipants(Long teamCalendarId, LocalDate date, List<Long> userIds) {
        LocalDateTime startDateTime = date.atStartOfDay();
        LocalDateTime endDateTime = date.atTime(23, 59, 59);

        List<Event> events = eventRepository.findEventsByDateAndParticipants(
                teamCalendarId, userIds, startDateTime, endDateTime
        );

        if (events.isEmpty()) {
            log.info("events size: {}", events.size());
        }

        List<EventJsonResponseDto> eventJsonResponseDtos = events.stream().map(EventJsonResponseDto::from).collect(Collectors.toList());
        return eventJsonResponseDtos;
    }
}
