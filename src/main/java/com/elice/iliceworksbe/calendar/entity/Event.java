package com.elice.iliceworksbe.calendar.entity;

import com.elice.iliceworksbe.calendar.dto.request.PostMyEventRequestDto;
import com.elice.iliceworksbe.calendar.dto.request.PostTeamEventRequestDto;
import com.elice.iliceworksbe.common.constant.Availability;
import com.elice.iliceworksbe.common.constant.PrivacyType;
import com.elice.iliceworksbe.common.entity.BaseEntity;
import com.elice.iliceworksbe.common.exception.BaseException;
import com.elice.iliceworksbe.common.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.envers.AuditOverride;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "EVENT")
@AuditOverride(forClass = BaseEntity.class)
public class Event extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id", nullable = false)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "dt_start_time", nullable = false)
    private LocalDateTime dtStartTime;

    @Column(name = "dt_end_time", nullable = false)
    private LocalDateTime dtEndTime;

    @Column(name = "is_all_day", nullable = false)
    private Boolean isAllDay;

    @Column(name = "privacy", nullable = false)
    @Enumerated(EnumType.STRING)
    private PrivacyType privacy;

    @Column(name = "availability", nullable = false)
    @Enumerated(EnumType.STRING)
    private Availability availability;

    @Column(name = "location")
    private String location;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "calendar_id")
    private Calendar calendar;


    public static Event of(PostTeamEventRequestDto postTeamEventRequestDto, Calendar calendar){

        // 3-0. 만약 종일에 표시돼있다면 시작 시각을 해당날짜 00시 00분 00초, 끝 시각을 해당날짜 23시 59분 59초로 설정
        // 팀 일정의 공개범위설정(privacy, availability)는 항상 PUBLIC, BUSY임
        if (!postTeamEventRequestDto.isAllDay()) {
            // 3-1. 일정에 대한 유효성 검사 (일정 끝 시간이 일정 시작 시간보다 앞선지 확인)
            if (postTeamEventRequestDto.dtStartTime().isAfter(postTeamEventRequestDto.dtEndTime())){
                throw new BaseException(ErrorCode.MUST_START_TIME_BEFORE_END_TIME);
            }

            return Event.builder()
                    .title(postTeamEventRequestDto.title())
                    .description(postTeamEventRequestDto.description())
                    .dtStartTime(postTeamEventRequestDto.dtStartTime())
                    .dtEndTime(postTeamEventRequestDto.dtEndTime())
                    .isAllDay(postTeamEventRequestDto.isAllDay())
                    .privacy(PrivacyType.PUBLIC)
                    .availability(Availability.BUSY)
                    .location(postTeamEventRequestDto.location())
                    .calendar(calendar)
                    .build();
        }

        return Event.builder()
                .title(postTeamEventRequestDto.title())
                .description(postTeamEventRequestDto.description())
                .dtStartTime(postTeamEventRequestDto.dtStartTime().toLocalDate().atStartOfDay())
                .dtEndTime(postTeamEventRequestDto.dtEndTime().toLocalDate().atTime(23, 59, 59))
                .isAllDay(postTeamEventRequestDto.isAllDay())
                .privacy(PrivacyType.PUBLIC)
                .availability(Availability.BUSY)
                .location(postTeamEventRequestDto.location())
                .calendar(calendar)
                .build();
    }

    public static Event of(PostMyEventRequestDto postMyEventRequestDto, Calendar calendar) {
        // 3-0. 만약 종일에 표시돼있다면 시작 시각을 해당날짜 00시 00분 00초, 끝 시각을 해당날짜 23시 59분 59초로 설정
        // 팀 일정의 공개범위설정(privacy, availability)는 항상 PUBLIC, BUSY임
        if (!postMyEventRequestDto.isAllDay()) {
            // 3-1. 일정에 대한 유효성 검사 (일정 끝 시간이 일정 시작 시간보다 앞선지 확인)
            if (postMyEventRequestDto.dtStartTime().isAfter(postMyEventRequestDto.dtEndTime())){
                throw new BaseException(ErrorCode.MUST_START_TIME_BEFORE_END_TIME);
            }

            return Event.builder()
                    .title(postMyEventRequestDto.title())
                    .description(postMyEventRequestDto.description())
                    .dtStartTime(postMyEventRequestDto.dtStartTime())
                    .dtEndTime(postMyEventRequestDto.dtEndTime())
                    .isAllDay(postMyEventRequestDto.isAllDay())
                    .privacy(postMyEventRequestDto.privacyType())
                    .availability(postMyEventRequestDto.availability())
                    .location(postMyEventRequestDto.location())
                    .calendar(calendar)
                    .build();
        }

        return Event.builder()
                .title(postMyEventRequestDto.title())
                .description(postMyEventRequestDto.description())
                .dtStartTime(postMyEventRequestDto.dtStartTime().toLocalDate().atStartOfDay())
                .dtEndTime(postMyEventRequestDto.dtEndTime().toLocalDate().atTime(23, 59, 59))
                .isAllDay(postMyEventRequestDto.isAllDay())
                .privacy(postMyEventRequestDto.privacyType())
                .availability(postMyEventRequestDto.availability())
                .location(postMyEventRequestDto.location())
                .calendar(calendar)
                .build();
    }
}
