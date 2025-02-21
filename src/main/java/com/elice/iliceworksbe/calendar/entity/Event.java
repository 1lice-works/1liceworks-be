package com.elice.iliceworksbe.calendar.entity;

import com.elice.iliceworksbe.calendar.dto.request.PostTeamEventRequestDto;
import com.elice.iliceworksbe.common.constant.Availability;
import com.elice.iliceworksbe.common.constant.PrivacyType;
import com.elice.iliceworksbe.common.entity.BaseEntity;
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

    @Column(name = "location", nullable = false)
    private String location;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "calendar_id")
    private Calendar calendar;


    public static Event of(PostTeamEventRequestDto postTeamEventRequestDto, Calendar calendar){
        return Event.builder()
                .title(postTeamEventRequestDto.title())
                .description(postTeamEventRequestDto.description())
                .dtStartTime(postTeamEventRequestDto.dtStartTime())
                .dtEndTime(postTeamEventRequestDto.dtEndTime())
                .isAllDay(postTeamEventRequestDto.isAllDay())
                .privacy(postTeamEventRequestDto.privacyType())
                .availability(postTeamEventRequestDto.availability())
                .location(postTeamEventRequestDto.location())
                .calendar(calendar)
                .build();
    }

}
