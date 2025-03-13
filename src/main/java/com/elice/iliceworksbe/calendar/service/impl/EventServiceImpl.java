package com.elice.iliceworksbe.calendar.service.impl;

import com.elice.iliceworksbe.auth.entity.User;
import com.elice.iliceworksbe.auth.repository.UserRepository;
import com.elice.iliceworksbe.calendar.dto.request.*;
import com.elice.iliceworksbe.calendar.dto.response.GetAccessibleCalendarsResponseDto;
import com.elice.iliceworksbe.calendar.dto.response.GetCalendarEventsResponseDto;
import com.elice.iliceworksbe.calendar.dto.response.GetEventsByTitleKeywordResponseDto;
import com.elice.iliceworksbe.calendar.dto.request.PatchMyEventRequestDto;
import com.elice.iliceworksbe.calendar.dto.request.PatchTeamEventRequestDto;
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
import com.elice.iliceworksbe.notification.service.impl.EventReminderServiceImpl;
import com.elice.iliceworksbe.notification.utils.NotificationMessages;
import com.elice.iliceworksbe.team.entity.Team;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
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

    @Override
    public GetEventsByTitleKeywordResponseDto getEventsByTitleKeyword(Long userId, GetEventsByTitleKeywordRequestDto getEventsByTitleKeywordRequestDto) {

        // 1. 현재 유저의 팀 조회
        Team team = getTeamByUserID(userId);

        // 2. calendarId들로 calendar 조회
        List<Calendar> calendars = calendarRepository.findAllById(getEventsByTitleKeywordRequestDto.calendarIds());

        // 3. 해당 캘린더들이 해당 팀의 캘린더가 맞는지 조회
        log.info("조회할 캘린더 개수 : {}", calendars.size());

        for (Calendar calendar : calendars) {
            if (!calendar.getTypeId().equals(-1L)) { // 법정공휴일 캘린더가 아닌 경우
                if (!calendar.getTeam().equals(team)) {
                    log.info("권한 없는 캘린더에 접근하려고 합니다.");
                    throw new BaseException(ErrorCode.NOT_FOUND_CALENDAR);
                }
            }
        }

        // 4. 해당 키워드를 포함하는 캘린더의 모든 일정 조회 (가장 최신 순으로 정렬)
        // 의문. 이런 경우 페이징을 어떻게 처리할 것인가?
        List<Event> resultEvents = new ArrayList<>();
        for (Calendar calendar : calendars) {
            if (calendar.getType().equals(CalendarType.MEMBER) && calendar.getTypeId().equals(userId)) { // 조회하려는 캘린더가 자신의 캘린더인 경우
                resultEvents.addAll(eventRepository.findByTitleContainingAndCalendar(getEventsByTitleKeywordRequestDto.keyword(), calendar));
            } else {
                resultEvents.addAll(eventRepository.findByTitleContainingAndCalendarAndNotPrivate(getEventsByTitleKeywordRequestDto.keyword(), calendar));
            }
        }

        resultEvents = resultEvents.stream().sorted(Comparator.comparing(Event::getDtStartTime).reversed()).toList();

        return GetEventsByTitleKeywordResponseDto.from(resultEvents);
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

    @Override
    public GetCalendarEventsResponseDto getCalendarEvents(Long requestingUserId, Long targetUserId, Long calendarId, int targetMonth, int targetYear, CalendarType calendarType) {

        log.info("CalendarType {}", calendarType);

        // 0. 현재 조회 시도하는 유저가 어느 팀에 존재하는지 확인
        Team queryingTeam = userRepository.findById(requestingUserId).orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND_USER)).getTeam();

        // 1. 선택한 캘린더에 대해 조회할 수 있는 권한이 있는지 확인. TypeId=-1은 법정공휴일 캘린더를 의미한다. (권한 상관없이 조회 가능)
        Calendar targetCalendar = calendarRepository.findById(calendarId).orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND_CALENDAR));

        if (!targetCalendar.getTypeId().equals(-1L)) {
            if (!(queryingTeam.equals(targetCalendar.getTeam()))) {
                log.info("조회하려는 캘린더의 typeId : {}, type : {}", targetCalendar.getTypeId(), targetCalendar.getType());
                throw new BaseException(ErrorCode.NOT_FOUND_CALENDAR);
            }
        }

        // 2. 조회할 일정 날짜 범위 구하기 (기준연월 기준 +- 1개월)
        EventDateRange eventDateRange = getEventDateRange(targetMonth, targetYear);

        switch (calendarType) {

            case MEMBER:
                log.info("Get Member Calendar {}", targetUserId);

                // 2. 해당 캘린더가 해당 유저의 캘린더인지 확인
                if (!(targetCalendar.getType().equals(calendarType) && targetCalendar.getTypeId().equals(targetUserId))) {
                    throw new BaseException(ErrorCode.NOT_FOUND_CALENDAR);
                }

                // 3. targetYear와 targetMonth를 기준으로 앞뒤로 1개월씩에 해당하는 모든 Event 조회
                List<Event> memberEvents = eventRepository.findEventsWithinThreeMonthsByCalendar(eventDateRange.startDateTime, eventDateRange.endDateTime, targetCalendar);

                // 4. 변환 후 전달

                // 4-1. 자신인 경우, 그대로 리턴
                if (targetUserId.equals(requestingUserId)) {
                    return new GetCalendarEventsResponseDto(calendarId, memberEvents.stream().map(GetCalendarEventsResponseDto.EventDto::from).toList());
                }

                // 4-2. 타인의 경우, 필터링하여 리턴
                return new GetCalendarEventsResponseDto(calendarId, memberEvents.stream().map(GetCalendarEventsResponseDto.EventDto::fromForMember).toList());

            case TEAM:
                log.info("Get Team Calendar {}", queryingTeam.getId());

                // 2. 해당 캘린더가 해당 팀의 캘린더인지 확인
                if (!(targetCalendar.getType().equals(calendarType) && targetCalendar.getTypeId().equals(queryingTeam.getId()))) {
                    throw new BaseException(ErrorCode.NOT_FOUND_CALENDAR);
                }

                // 3. targetYear와 targetMonth를 기준으로 앞뒤로 1개월씩에 해당하는 모든 Event 조회
                List<Event> teamEvents = eventRepository.findEventsWithinThreeMonthsByCalendar(eventDateRange.startDateTime, eventDateRange.endDateTime, targetCalendar);

                // 4. 변환 후 전달
                return new GetCalendarEventsResponseDto(calendarId, teamEvents.stream().map(GetCalendarEventsResponseDto.EventDto::from).toList());

            case OTHER:
                log.info("Get Other Calendar");

                // 2. 해당 캘린더가 법정 공휴일 캘린더인지 확인
                if (!(targetCalendar.getType().equals(calendarType) && targetCalendar.getTypeId().equals(-1L))) {
                    throw new BaseException(ErrorCode.NOT_FOUND_CALENDAR);
                }

                // 3. targetYear와 targetMonth를 기준으로 앞뒤로 1개월씩에 해당하는 모든 Event 조회
                List<Event> otherEvents = eventRepository.findEventsWithinThreeMonthsByCalendar(eventDateRange.startDateTime, eventDateRange.endDateTime, targetCalendar);

                // 4. 변환 후 전달
                return new GetCalendarEventsResponseDto(calendarId, otherEvents.stream().map(GetCalendarEventsResponseDto.EventDto::from).toList());

            default:
                throw new BaseException(ErrorCode.NOT_FOUND_CALENDAR);
        }

    }

    @Override
    public List<GetAccessibleCalendarsResponseDto> getAccessibleCalendars(Long userId) {

        // 0. 현재 조회 시도하는 유저가 어느 팀에 존재하는지 확인
        Team queryingTeam = userRepository.findById(userId).orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND_USER)).getTeam();

        // 1. 해당 팀의 캘린더들 조회
        List<Calendar> teamAccessibleCalendars = calendarRepository.findByTeam(queryingTeam);

        // 2. 그 밖의 캘린더들 조회
        List<Calendar> otherCalendars = calendarRepository.findByTypeId(-1L);

        List<Calendar> allCalendars = new ArrayList<>();
        allCalendars.addAll(teamAccessibleCalendars);
        allCalendars.addAll(otherCalendars);

        return allCalendars.stream()
                .map(calendar -> GetAccessibleCalendarsResponseDto.from(calendar, userId))
                .toList();
    }

    private EventDateRange getEventDateRange(int targetMonth, int targetYear) {
        // 기준 월의 첫날과 마지막 날
        YearMonth targetYM = YearMonth.of(targetYear, targetMonth);

        // 이전 월의 첫날
        YearMonth prevYM = targetYM.minusMonths(1);
        LocalDate startOfPrevMonth = prevYM.atDay(1);

        // 다음 월의 마지막 날
        YearMonth nextYM = targetYM.plusMonths(1);
        LocalDate endOfNextMonth = nextYM.atEndOfMonth();

        // 최종 검색 범위
        LocalDateTime startDate = startOfPrevMonth.atStartOfDay();  // 2024-12-01 00:00:00
        LocalDateTime endDate = endOfNextMonth.atTime(23, 59, 59);  // 2025-02-28 23:59:59

        return new EventDateRange(startDate, endDate);
    }

    private record EventDateRange(LocalDateTime startDateTime, LocalDateTime endDateTime){}

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
