package com.elice.iliceworksbe.common.utils;

import com.elice.iliceworksbe.auth.entity.User;
import com.elice.iliceworksbe.auth.repository.UserRepository;
import com.elice.iliceworksbe.calendar.entity.Calendar;
import com.elice.iliceworksbe.calendar.entity.Event;
import com.elice.iliceworksbe.calendar.entity.EventParticipant;
import com.elice.iliceworksbe.calendar.repository.CalendarRepository;
import com.elice.iliceworksbe.calendar.repository.EventParticipantRepository;
import com.elice.iliceworksbe.calendar.repository.EventRepository;
import com.elice.iliceworksbe.common.constant.*;
import com.elice.iliceworksbe.notification.entity.EventReminder;
import com.elice.iliceworksbe.notification.entity.Notification;
import com.elice.iliceworksbe.notification.entity.Webhook;
import com.elice.iliceworksbe.notification.repository.EventReminderRepository;
import com.elice.iliceworksbe.notification.repository.NotificationRepository;
import com.elice.iliceworksbe.notification.repository.WebhookRepository;
import com.elice.iliceworksbe.team.constant.Industry;
import com.elice.iliceworksbe.team.constant.Scale;
import com.elice.iliceworksbe.team.entity.*;
import com.elice.iliceworksbe.team.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.List;

/*
서버 데이터 초기화용
 */

@Component
@RequiredArgsConstructor
public class DataLoader {

    private final TeamRepository teamRepository;
    private final PasswordEncoder passwordEncoder;
    private final CalendarRepository calendarRepository;
    private final EmployeeRepository employeeRepository;
    private final PositionRepository positionRepository;
    private final JobTitleRepository jobTitleRepository;
    private final UserTypeRepository userTypeRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final EventParticipantRepository eventParticipantRepository;
    private final EventReminderRepository eventReminderRepository;
    private final NotificationRepository notificationRepository;
    private final WebhookRepository webhookRepository;

    @PostConstruct
    public void init() {

        Team iliceTeamBE = Team.builder()
                .companyName("일리스웍스 컴퍼니")
                .teamName("일리스 BE팀")
                .domainName("threadly.ilice-works.com")
                .hasPrivateDomain(true)
                .industry(Industry.CONSTRUCTION)
                .scale(Scale.TEN_TO_NINETEEN)
                .build();

        Team iliceTeamFE = Team.builder()
                .companyName("일리스웍스 컴퍼니")
                .teamName("일리스 FE팀")
                .domainName("threadly.ilice-works.com")
                .hasPrivateDomain(true)
                .industry(Industry.CONSTRUCTION)
                .scale(Scale.TEN_TO_NINETEEN)
                .build();

        teamRepository.save(iliceTeamBE);
        teamRepository.save(iliceTeamFE);

        Calendar calendarBE = Calendar.builder()
                .name("일리스 BE팀 캘린더")
                .type(CalendarType.TEAM)
                .typeId(iliceTeamBE.getId())
                .team(iliceTeamBE)
                .build();

        Calendar calendarFE = Calendar.builder()
                .name("일리스 FE팀 캘린더")
                .type(CalendarType.TEAM)
                .typeId(iliceTeamFE.getId())
                .team(iliceTeamFE)
                .build();

        calendarRepository.save(calendarBE);
        calendarRepository.save(calendarFE);

        Position staffPosition = Position.builder()
                .name("사원")
                .build();
        JobTitle generalJobTitle = JobTitle.builder()
                .name("일반직")
                .build();
        UserType regularUserType = UserType.builder()
                .name("정규직")
                .build();

        positionRepository.save(staffPosition);
        jobTitleRepository.save(generalJobTitle);
        userTypeRepository.save(regularUserType);

        User taeseungUser = User.builder()
                .accountId("hi563@threadly.ilice-works.com")
                .profileImage("https://d2u3dcdbebyaiu.cloudfront.net/uploads/atch_img/554/20a6459f319fff68f0f90ce8c62d9be0_res.jpeg")
                .username("정태승")
                .password(passwordEncoder.encode("!a12345678"))
                .privateEmail("hi563@naver.com")
                .role(Role.LEADER)
                .status(Status.ACTIVE)
                .team(iliceTeamBE)
                .isTeamCreated(true)
                .build();

        User kwanghoUser = User.builder()
                .accountId("kwangho@threadly.ilice-works.com")
                .profileImage("https://cdn-icons-png.flaticon.com/512/11820/11820363.png")
                .username("명광호")
                .password(passwordEncoder.encode("!a12345678"))
                .privateEmail("kwangho@naver.com")
                .role(Role.MEMBER)
                .status(Status.ACTIVE)
                .team(iliceTeamBE)
                .isTeamCreated(true)
                .build();

        User soogyeongUser = User.builder()
                .accountId("soogyeong@threadly.ilice-works.com")
                .username("엄수경")
                .password(passwordEncoder.encode("!a12345678"))
                .privateEmail("soogyeong@naver.com")
                .role(Role.MEMBER)
                .status(Status.ACTIVE)
                .team(iliceTeamBE)
                .isTeamCreated(true)
                .build();

        User hyerimUser = User.builder()
                .accountId("hyerim@threadly.ilice-works.com")
                .profileImage("https://cdn-icons-png.flaticon.com/512/3135/3135789.png")
                .username("양혜림")
                .password(passwordEncoder.encode("!a12345678"))
                .privateEmail("hyerim@naver.com")
                .role(Role.LEADER)
                .status(Status.ACTIVE)
                .team(iliceTeamFE)
                .isTeamCreated(true)
                .build();

        User kyungjunUser = User.builder()
                .accountId("kyungjun@threadly.ilice-works.com")
                .profileImage("https://cdn-icons-png.flaticon.com/256/11045/11045219.png")
                .username("정경준")
                .password(passwordEncoder.encode("!a12345678"))
                .privateEmail("kyungjun@naver.com")
                .role(Role.MEMBER)
                .status(Status.ACTIVE)
                .team(iliceTeamFE)
                .isTeamCreated(true)
                .build();

        Employee taeseungEmployee = Employee.builder()
                .employeeNumber("24-12345")
                .hireDate(LocalDateTime.now())
                .position(staffPosition)
                .jobTitle(generalJobTitle)
                .userType(regularUserType)
                .responsibility("백엔드 개발")
                .user(taeseungUser)
                .build();

        Employee kwanghoEmployee = Employee.builder()
                .employeeNumber("24-12347")
                .hireDate(LocalDateTime.now())
                .position(staffPosition)
                .jobTitle(generalJobTitle)
                .userType(regularUserType)
                .responsibility("백엔드 개발")
                .user(kwanghoUser)
                .build();


        Employee soogyeongEmployee = Employee.builder()
                .employeeNumber("24-12347")
                .hireDate(LocalDateTime.now())
                .position(staffPosition)
                .jobTitle(generalJobTitle)
                .userType(regularUserType)
                .responsibility("백엔드 개발")
                .user(soogyeongUser)
                .build();


        Employee hyerimEmployee = Employee.builder()
                .employeeNumber("24-12346")
                .hireDate(LocalDateTime.now())
                .position(staffPosition)
                .jobTitle(generalJobTitle)
                .userType(regularUserType)
                .responsibility("없음")
                .user(hyerimUser)
                .build();

        Employee kyungjunEmployee = Employee.builder()
                .employeeNumber("24-12347")
                .hireDate(LocalDateTime.now())
                .position(staffPosition)
                .jobTitle(generalJobTitle)
                .userType(regularUserType)
                .responsibility("없음")
                .user(kyungjunUser)
                .build();


        // user 저장
        userRepository.save(taeseungUser);
        userRepository.save(soogyeongUser);
        userRepository.save(kwanghoUser);
        userRepository.save(hyerimUser);
        userRepository.save(kyungjunUser);

        // employee 저장
        taeseungUser.setEmployee(taeseungEmployee);
        soogyeongUser.setEmployee(soogyeongEmployee);
        kwanghoUser.setEmployee(kwanghoEmployee);
        hyerimUser.setEmployee(hyerimEmployee);
        kyungjunUser.setEmployee(kyungjunEmployee);

        employeeRepository.save(taeseungEmployee);
        employeeRepository.save(soogyeongEmployee);
        employeeRepository.save(kwanghoEmployee);
        employeeRepository.save(hyerimEmployee);
        employeeRepository.save(kyungjunEmployee);

        Calendar calendarTaeseung = Calendar.builder()
                .name("태승 / 직급1")
                .type(CalendarType.MEMBER)
                .typeId(taeseungUser.getId())
                .team(iliceTeamBE)
                .build();

        Calendar calendarKwangho = Calendar.builder()
                .name("광호 / 직급2")
                .type(CalendarType.MEMBER)
                .typeId(kwanghoUser.getId())
                .team(iliceTeamBE)
                .build();

        Calendar calendarSugyeong = Calendar.builder()
                .name("수경 / 직급3")
                .type(CalendarType.MEMBER)
                .typeId(soogyeongUser.getId())
                .team(iliceTeamBE)
                .build();

        Calendar calendarHyerim = Calendar.builder()
                .name("혜림 / 직급2")
                .type(CalendarType.MEMBER)
                .typeId(hyerimUser.getId())
                .team(iliceTeamFE)
                .build();

        Calendar calendarKyungjun = Calendar.builder()
                .name("경준 / 직급2")
                .type(CalendarType.MEMBER)
                .typeId(kyungjunUser.getId())
                .team(iliceTeamFE)
                .build();


        // 법정 공휴일 캘린더
        Calendar otherCalendar = Calendar.builder()
                .name("법정 공휴일")
                .type(CalendarType.OTHER)
                .typeId(-1L)
                .build();

        calendarRepository.saveAll(List.of(calendarTaeseung, calendarKwangho, calendarSugyeong, calendarHyerim, calendarKyungjun, otherCalendar));

        Event ev1BE = Event.builder()
                .title("팀BE 주간 회의1")
                .description("스프린트 진행.")
                .dtStartTime(LocalDateTime.of(2025, 2, 24, 10, 0))
                .dtEndTime(LocalDateTime.of(2025, 2, 28, 11, 30))
                .isAllDay(false)
                .privacy(PrivacyType.PUBLIC)
                .availability(Availability.BUSY)
                .location("A동 회의실 11-1")
                .calendar(calendarBE)
                .build();

        Event ev2BE = Event.builder()
                .title("백엔드 기술 공유 세션")
                .description("새로운 기술 트렌드에 대한 논의")
                .dtStartTime(LocalDateTime.of(2025, 3, 5, 14, 0))
                .dtEndTime(LocalDateTime.of(2025, 3, 5, 16, 0))
                .isAllDay(false)
                .privacy(PrivacyType.PUBLIC)
                .availability(Availability.BUSY)
                .location("A동 회의실 11-1")
                .calendar(calendarBE)
                .build();

        Event ev3BE = Event.builder()
                .title("팀 점심 회식")
                .description("올해의 성과를 기념하는 식사")
                .dtStartTime(LocalDateTime.of(2025, 2, 20, 12, 30))
                .dtEndTime(LocalDateTime.of(2025, 2, 20, 14, 0))
                .isAllDay(false)
                .privacy(PrivacyType.PRIVATE)
                .availability(Availability.FREE)
                .location("강남역 근처 식당")
                .calendar(calendarBE)
                .build();

        Event ev1FE = Event.builder()
                .title("FE-BE 협업 미팅")
                .description("API 스펙 조율 및 개선 논의")
                .dtStartTime(LocalDateTime.of(2025, 1, 21, 10, 0))
                .dtEndTime(LocalDateTime.of(2025, 1, 21, 12, 0))
                .isAllDay(false)
                .privacy(PrivacyType.PUBLIC)
                .availability(Availability.BUSY)
                .location("A동 회의실 10-3")
                .calendar(calendarFE)
                .build();

        Event ev4BE = Event.builder()
                .title("시스템 모니터링 점검")
                .description("서버 성능 및 로그 분석")
                .dtStartTime(LocalDateTime.of(2025, 4, 10, 16, 30))
                .dtEndTime(LocalDateTime.of(2025, 4, 10, 18, 30))
                .isAllDay(false)
                .privacy(PrivacyType.PUBLIC)
                .availability(Availability.BUSY)
                .location("B동 모니터링 센터")
                .calendar(calendarBE)
                .build();

        Event ev1Taeseung = Event.builder()
                .title("팀 리더 전략 회의")
                .description("다음 분기 목표 설정")
                .dtStartTime(LocalDateTime.of(2025, 1, 16, 11, 0))
                .dtEndTime(LocalDateTime.of(2025, 1, 16, 13, 0))
                .isAllDay(false)
                .privacy(PrivacyType.PRIVATE)
                .availability(Availability.BUSY)
                .location("CEO 회의실")
                .calendar(calendarTaeseung)
                .build();

        Event ev5BE = Event.builder()
                .title("개발자 커뮤니티 밋업")
                .description("기술 공유 및 네트워킹")
                .dtStartTime(LocalDateTime.of(2025, 3, 8, 19, 0))
                .dtEndTime(LocalDateTime.of(2025, 3, 8, 21, 0))
                .isAllDay(false)
                .privacy(PrivacyType.PUBLIC)
                .availability(Availability.FREE)
                .location("온라인 Zoom")
                .calendar(calendarBE)
                .build();

        Event ev6BE = Event.builder()
                .title("DB 성능 최적화 세미나")
                .description("데이터베이스 인덱싱 및 쿼리 튜닝")
                .dtStartTime(LocalDateTime.of(2025, 4, 2, 15, 0))
                .dtEndTime(LocalDateTime.of(2025, 4, 2, 17, 0))
                .isAllDay(false)
                .privacy(PrivacyType.PUBLIC)
                .availability(Availability.BUSY)
                .location("C동 세미나실")
                .calendar(calendarBE)
                .build();

        Event ev7BE = Event.builder()
                .title("팀원 1:1 면담")
                .description("개발 진행 상황 체크 및 피드백")
                .dtStartTime(LocalDateTime.of(2025, 2, 12, 14, 0))
                .dtEndTime(LocalDateTime.of(2025, 2, 12, 15, 30))
                .isAllDay(false)
                .privacy(PrivacyType.PRIVATE)
                .availability(Availability.BUSY)
                .location("리더 오피스")
                .calendar(calendarBE)
                .build();

        Event ev2FE = Event.builder()
                .title("UI/UX 개선 논의")
                .description("디자인 시스템 및 피드백 반영")
                .dtStartTime(LocalDateTime.of(2025, 3, 3, 9, 30))
                .dtEndTime(LocalDateTime.of(2025, 3, 3, 11, 30))
                .isAllDay(false)
                .privacy(PrivacyType.PUBLIC)
                .availability(Availability.BUSY)
                .location("디자인팀 회의실")
                .calendar(calendarFE)
                .build();

        Event ev1kwangho = Event.builder()
                .title("치과 가기")
                .description("보라치과")
                .dtStartTime(LocalDateTime.of(2025, 1, 25, 10, 0))
                .dtEndTime(LocalDateTime.of(2025, 1, 25, 11, 30))
                .isAllDay(false)
                .privacy(PrivacyType.PUBLIC)
                .availability(Availability.BUSY)
                .location("전국")
                .calendar(calendarKwangho)
                .build();

        Event ev2kwangho = Event.builder()
                .title("건강검진")
                .description("정기 건강검진")
                .dtStartTime(LocalDateTime.of(2025, 2, 14, 8, 30))
                .dtEndTime(LocalDateTime.of(2025, 2, 14, 10, 30))
                .isAllDay(false)
                .privacy(PrivacyType.PRIVATE)
                .availability(Availability.BUSY)
                .location("서울 강남 병원")
                .calendar(calendarKwangho)
                .build();

        Event ev3kwangho = Event.builder()
                .title("자격증 시험")
                .description("정보처리기사 실기 시험")
                .dtStartTime(LocalDateTime.of(2025, 3, 15, 13, 0))
                .dtEndTime(LocalDateTime.of(2025, 3, 15, 15, 30))
                .isAllDay(false)
                .privacy(PrivacyType.PUBLIC)
                .availability(Availability.BUSY)
                .location("코엑스 컨벤션센터")
                .calendar(calendarKwangho)
                .build();

        Event ev4kwangho = Event.builder()
                .title("친구 결혼식")
                .description("대학교 동기 결혼식")
                .dtStartTime(LocalDateTime.of(2025, 4, 5, 12, 0))
                .dtEndTime(LocalDateTime.of(2025, 4, 5, 14, 0))
                .isAllDay(false)
                .privacy(PrivacyType.PUBLIC)
                .availability(Availability.FREE)
                .location("서울 신라호텔")
                .calendar(calendarKwangho)
                .build();



        // 이벤트 저장
        List<Event> events = List.of(ev1BE, ev2BE, ev3BE, ev1FE, ev4BE, ev1Taeseung, ev5BE, ev6BE, ev7BE, ev2FE,
                ev1kwangho, ev2kwangho, ev3kwangho, ev4kwangho);
        eventRepository.saveAll(events);


        EventParticipant ep1 = EventParticipant.builder()
                .event(ev1BE)
                .user(taeseungUser)
                .build();

        EventParticipant ep1_1 = EventParticipant.builder()
                .event(ev1BE)
                .user(kwanghoUser)
                .build();

        EventParticipant ep2 = EventParticipant.builder()
                .event(ev2BE)
                .user(taeseungUser)
                .build();

        EventParticipant ep2_1 = EventParticipant.builder()
                .event(ev2BE)
                .user(kwanghoUser)
                .build();

        EventParticipant ep2_2 = EventParticipant.builder()
                .event(ev2BE)
                .user(soogyeongUser)
                .build();

        EventParticipant ep3 = EventParticipant.builder()
                .event(ev3BE)
                .user(taeseungUser)
                .build();

        EventParticipant ep3_1 = EventParticipant.builder()
                .event(ev3BE)
                .user(kwanghoUser)
                .build();

        EventParticipant ep3_2 = EventParticipant.builder()
                .event(ev3BE)
                .user(soogyeongUser)
                .build();

        EventParticipant ep4 = EventParticipant.builder()
                .event(ev1FE)
                .user(hyerimUser)
                .build();

        EventParticipant ep5 = EventParticipant.builder()
                .event(ev4BE)
                .user(taeseungUser)
                .build();

        EventParticipant ep6 = EventParticipant.builder()
                .event(ev1Taeseung)
                .user(taeseungUser)
                .build();

        EventParticipant ep7 = EventParticipant.builder()
                .event(ev5BE)
                .user(kwanghoUser)
                .build();

        EventParticipant ep8 = EventParticipant.builder()
                .event(ev6BE)
                .user(kwanghoUser)
                .build();

        EventParticipant ep9 = EventParticipant.builder()
                .event(ev7BE)
                .user(kwanghoUser)
                .build();

        EventParticipant ep10 = EventParticipant.builder()
                .event(ev2FE)
                .user(kyungjunUser)
                .build();

        EventParticipant ep11 = EventParticipant.builder()
                .event(ev1kwangho)
                .user(kwanghoUser)
                .build();

        EventParticipant ep12 = EventParticipant.builder()
                .event(ev2kwangho)
                .user(kwanghoUser)
                .build();

        EventParticipant ep13 = EventParticipant.builder()
                .event(ev3kwangho)
                .user(kwanghoUser)
                .build();

        EventParticipant ep14 = EventParticipant.builder()
                .event(ev4kwangho)
                .user(kwanghoUser)
                .build();

        eventParticipantRepository.saveAll(List.of(ep1, ep1_1, ep2, ep2_1, ep2_2, ep3, ep3_1, ep3_2, ep4, ep5, ep6, ep7, ep8, ep9, ep10, ep11, ep12, ep13, ep14));

        //이벤트 리마인더 저장
        EventReminder ev1BE_er1 = EventReminder.builder()
                .notifyTime(ev1BE.getDtStartTime().minusMinutes(30))
                .event(ev1BE)
                .build();

        EventReminder ev1BE_er2 = EventReminder.builder()
                .notifyTime(ev1BE.getDtStartTime().minusDays(1))
                .event(ev1BE)
                .build();

        EventReminder ev2BE_er1 = EventReminder.builder()
                .notifyTime(ev1BE.getDtStartTime().minusDays(1))
                .event(ev2BE)
                .build();

        EventReminder ev1FE_er1 = EventReminder.builder()
                .notifyTime(ev1BE.getDtStartTime().minusDays(1))
                .event(ev1FE)
                .build();

        EventReminder ev1FE_er2 = EventReminder.builder()
                .notifyTime(ev1BE.getDtStartTime().minusMinutes(30))
                .event(ev1FE)
                .build();

        EventReminder ev1Taeseung_er1 = EventReminder.builder()
                .notifyTime(ev1BE.getDtStartTime().minusHours(1))
                .event(ev1Taeseung)
                .build();

        eventReminderRepository.saveAll(List.of(ev1BE_er1, ev1BE_er2, ev2BE_er1, ev1FE_er1, ev1FE_er2, ev1Taeseung_er1));

        //Notification 저장
        Notification ev1BE_er1_noti = Notification.builder()
                .message(ev1BE.getTitle())
                .notifyTime(ev1BE_er1.getNotifyTime())
                .user(ep1.getUser())
                .build();

        Notification ev1BE_er2_noti = Notification.builder()
                .message(ev1BE.getTitle())
                .notifyTime(ev1BE_er2.getNotifyTime())
                .user(ep1.getUser())
                .build();

        Notification ev2BE_er1_noti = Notification.builder()
                .message(ev2BE.getTitle())
                .notifyTime(ev2BE_er1.getNotifyTime())
                .user(ep2.getUser())
                .build();

        Notification ev1FE_er1_noti = Notification.builder()
                .message(ev1FE.getTitle())
                .notifyTime(ev1FE_er1.getNotifyTime())
                .user(ep4.getUser())
                .build();

        Notification ev1FE_er2_noti = Notification.builder()
                .message(ev1FE.getTitle())
                .notifyTime(ev1FE_er2.getNotifyTime())
                .user(ep4.getUser())
                .build();

        Notification ev1Taeseung_er1_noti = Notification.builder()
                .message(ev1Taeseung.getTitle())
                .notifyTime(ev1Taeseung_er1.getNotifyTime())
                .user(ep6.getUser())
                .build();

        notificationRepository.saveAll(List.of(ev1BE_er1_noti, ev1BE_er2_noti, ev2BE_er1_noti, ev1FE_er1_noti, ev1FE_er2_noti, ev1Taeseung_er1_noti));

        //웹훅 저장
        Webhook iliceTeamBE_webhook = Webhook.builder()
                .payloadUrl("https://discord.com/api/webhooks/1342348107215798306/bYtBuXjzlkFdsj2saJ03Mo5GvZUCPaGRFB7EsK7no01urQ61OnPMqvZnY2XJkewuRTcn")
                .contentType(ContentType.APPLICATION_JSON)
                .calendar(calendarBE)
                .build();

        webhookRepository.save(iliceTeamBE_webhook);

    }

}
