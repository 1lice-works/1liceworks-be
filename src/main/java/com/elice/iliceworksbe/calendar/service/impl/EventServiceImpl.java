package com.elice.iliceworksbe.calendar.service.impl;

import com.elice.iliceworksbe.auth.entity.User;
import com.elice.iliceworksbe.auth.repository.UserRepository;
import com.elice.iliceworksbe.calendar.dto.request.PostTeamEventRequestDto;
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
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
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
                .orElseThrow(() -> new BaseException(ErrorCode.NOT_FIND_USER))
                .getTeam();

        // 2. CalendarId가 해당 유저의 팀 캘린더가 맞는지 조회
        Calendar teamCalendar = calendarRepository.findById(calendarId)
                .orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND_CALENDAR));

        if (!(teamCalendar.getId().equals(team.getId()) && teamCalendar.getType().equals(CalendarType.TEAM))) {
            throw new BaseException(ErrorCode.NOT_FOUND_CALENDAR);
        }

        // 3. 해당 팀 캘린더에 대해 일정 생성 및 추가
        // 3-1. 일정에 대한 유효성 검사 (일정 끝 시간이 일정 시작 시간보다 앞선지 확인)
        if (postTeamEventRequestDto.dtStartTime().isAfter(postTeamEventRequestDto.dtEndTime())){
            throw new BaseException(ErrorCode.MUST_START_TIME_BEFORE_END_TIME);
        }

        // 3-2. 일정 저장
        Event teamEvent = Event.of(postTeamEventRequestDto, teamCalendar);
        eventRepository.save(teamEvent);

        // 4. 생성된 EventReminder 추가
        List<EventReminder> eventReminders = postTeamEventRequestDto.eventReminders()
                .stream()
                .map(EventReminder::from)
                .toList();
        eventReminderRepository.saveAll(eventReminders);

        // 5. 일정 참석자 추가
        // 5-1. 일정 인원 조회
        List<User> users = userRepository.findAllById(
                postTeamEventRequestDto.eventParticipants().stream().map(
                        PostTeamEventRequestDto.EventParticipantDto::userId
                ).toList()
        );
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
}
