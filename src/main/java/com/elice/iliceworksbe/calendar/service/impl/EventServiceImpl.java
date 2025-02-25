package com.elice.iliceworksbe.calendar.service.impl;

import com.elice.iliceworksbe.auth.entity.User;
import com.elice.iliceworksbe.auth.repository.UserRepository;
import com.elice.iliceworksbe.calendar.dto.request.PatchMyEventRequestDto;
import com.elice.iliceworksbe.calendar.dto.request.PatchTeamEventRequestDto;
import com.elice.iliceworksbe.calendar.dto.request.PostMyEventRequestDto;
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
import com.elice.iliceworksbe.notification.service.impl.EventReminderServiceImpl;
import com.elice.iliceworksbe.notification.utils.NotificationMessages;
import com.elice.iliceworksbe.team.entity.Team;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final CalendarRepository calendarRepository;
    private final UserRepository userRepository;
    private final EventReminderRepository eventReminderRepository;
    private final EventParticipantRepository eventParticipantRepository;
    private final NotificationService notificationService;
    private final EventReminderServiceImpl eventReminderServiceImpl;

    @Override
    @Transactional
    public void postTeamEvent(Long userId, Long calendarId, PostTeamEventRequestDto postTeamEventRequestDto) {

        // 1. 현재 유저의 팀 조회
        Team team = getTeamByUserID(userId);

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
    @Transactional
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
    @Transactional
    public void deleteTeamEvent(Long userId, Long calendarId, Long eventId) {

        // 1. 현재 유저의 팀 조회
        Team team = getTeamByUserID(userId);

        // 2. CalendarId가 해당 유저의 팀 캘린더가 맞는지 조회
        Calendar teamCalendar = calendarRepository.findById(calendarId)
                .orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND_CALENDAR));

        if (!(teamCalendar.getId().equals(team.getId()) && teamCalendar.getType().equals(CalendarType.TEAM))) {
            throw new BaseException(ErrorCode.NOT_FOUND_CALENDAR);
        }

        // 3. 해당 일정이 해당 팀의 Calendar에 맞는지 확인
        validateEventInCalendar(eventId, teamCalendar);

        // 4. 일정 삭제
        deleteEventByEventId(eventId);
    }

    @Override
    @Transactional
    public void deleteMyEvent(Long userId, Long eventId) {
        // 1. 현재 유저의 캘린더 조회
        User currentUser = userRepository.findById(userId).orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND_USER));
        Calendar myCalendar = calendarRepository.findByTypeAndTypeId(CalendarType.MEMBER, userId)
                .orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND_CALENDAR));

        // 2. 해당 이벤트가 현재 캘린더의 이벤트가 맞는지 확인
        validateEventInCalendar(eventId, myCalendar);

        // 3. 일정 삭제
        deleteEventByEventId(eventId);
    }

    @Override
    @Transactional
    public void patchTeamEvent(Long userId, Long calendarId, Long eventId, PatchTeamEventRequestDto patchTeamEventRequestDto) {

        // 1. 현재 유저의 팀 조회
        Team team = getTeamByUserID(userId);

        // 2. CalendarId가 해당 유저의 팀 캘린더가 맞는지 조회
        Calendar teamCalendar = calendarRepository.findById(calendarId)
                .orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND_CALENDAR));

        if (!(teamCalendar.getId().equals(team.getId()) && teamCalendar.getType().equals(CalendarType.TEAM))) {
            throw new BaseException(ErrorCode.NOT_FOUND_CALENDAR);
        }

        // 3. 해당 일정이 해당 팀의 Calendar에 맞는지 확인
        Event targetEvent = eventRepository.findById(eventId)
                .orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND_EVENT));
        if (!teamCalendar.equals(targetEvent.getCalendar())) {
            throw new BaseException(ErrorCode.NOT_FOUND_EVENT);
        }

        // 4. 해당 일정 정보 수정
        targetEvent.patchTeamEvent(patchTeamEventRequestDto);

        // 5. 해당 일정의 EventReminder, EventParticipant 삭제
        removeEventDependencies(eventId);

        // 6. EventReminder 추가
        List<EventReminder> eventReminders = patchTeamEventRequestDto.eventReminders()
                .stream()
                .map(eR -> EventReminder.of(eR, targetEvent))
                .toList();
        eventReminderRepository.saveAll(eventReminders);

        // 7. 일정 참석자 추가
        // 7-1. 일정 인원 조회
        List<Long> participantIds = patchTeamEventRequestDto.eventParticipants().stream()
                .map(PatchTeamEventRequestDto.EventParticipantDto::userId)
                .toList();

        List<User> users = userRepository.findAllById(participantIds);

        // 7-2. 현재 사용자가 일정 참석자 리스트에 포함되어 있지 않다면 users 리스트에 추가
        if (!participantIds.contains(userId)) {
            User currentUser = userRepository.findById(userId)
                    .orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND_USER));
            users.add(currentUser);
        }

        // 7-3. 일정 인원 토대로 일정 참석자 추가 (일정 참석자가 해당 팀원인지 확인하는 로직추가)
        List<EventParticipant> eventParticipants = users.stream()
                .filter(participant -> participant.checkMyTeam(team))
                .map(participant -> EventParticipant.of(targetEvent, participant)).toList();
        eventParticipantRepository.saveAll(eventParticipants);

    }

    @Override
    @Transactional
    public void patchMyEvent(Long userId, Long eventId, PatchMyEventRequestDto patchMyEventRequestDto) {

        // 1. 현재 유저의 캘린더 조회
        Calendar myCalendar = calendarRepository.findByTypeAndTypeId(CalendarType.MEMBER, userId)
                .orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND_CALENDAR));

        // 2. 해당 이벤트가 내 캘린더의 이벤트가 맞는지 확인
        Event myEvent = eventRepository.findById(eventId)
                .orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND_EVENT));
        if (!myCalendar.equals(myEvent.getCalendar())) {
            throw new BaseException(ErrorCode.NOT_FOUND_EVENT);
        }

        // 3. 일정 수정
        myEvent.patchMyEvent(patchMyEventRequestDto);

        // 4. 기존 EventReminder 삭제
        deleteEventReminder(eventId);

        // 5. EventReminder 추가
        List<EventReminder> eventReminders = patchMyEventRequestDto.eventReminders()
                .stream()
                .map(eR -> EventReminder.of(eR, myEvent))
                .toList();
        eventReminderRepository.saveAll(eventReminders);
    }

    private Team getTeamByUserID(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND_USER))
                .getTeam();
    }

    private void validateEventInCalendar(Long eventId, Calendar calendar) {
        Event targetEvent = eventRepository.findById(eventId)
                .orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND_EVENT));
        if (!calendar.equals(targetEvent.getCalendar())) {
            throw new BaseException(ErrorCode.NOT_FOUND_EVENT);
        }
    }

    private void deleteEventByEventId(Long eventId) {
        // 해당 일정의 참석자, EventReminder 삭제
        removeEventDependencies(eventId);

        // 해당 팀 캘린더에 대한 일정 삭제
        eventRepository.deleteById(eventId);
    }

    private void removeEventDependencies(Long eventId) {
        // 해당 일정의 일정 참석자들 삭제
        eventParticipantRepository.deleteByEventId(eventId);

        deleteEventReminder(eventId);
    }

    private void deleteEventReminder(Long eventId) {
        eventReminderServiceImpl.deleteAllEventReminderByEventId(eventId);
    }
}
