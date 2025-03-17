package com.elice.iliceworksbe.calendar.dto.response;

import com.elice.iliceworksbe.calendar.entity.Event;
import com.elice.iliceworksbe.calendar.entity.EventParticipant;
import com.elice.iliceworksbe.common.constant.Availability;
import com.elice.iliceworksbe.common.constant.PrivacyType;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

public record GetCalendarEventsResponseDto(
        Long calendarId,
        List<EventDto> eventDtos
) {

    @Builder
    public record EventDto(
        Long eventId,
        String title,
        String description,
        LocalDateTime dtStartTime,
        LocalDateTime dtEndTime,
        Boolean isAllDay,
        PrivacyType privacyType,
        Availability availability,
        String location,
        List<Long> participants
    ){
        public static EventDto fromForMember(Event event){

            if (event.getPrivacy().equals(PrivacyType.PRIVATE)){
                return EventDto.builder()
                        .eventId(event.getId())
                        .dtStartTime(event.getDtStartTime())
                        .dtEndTime(event.getDtEndTime())
                        .isAllDay(event.getIsAllDay())
                        .privacyType(event.getPrivacy())
                        .availability(event.getAvailability())
                        .build();
            }

            return from(event);
        }

        public static EventDto fromForTeam(Event event, List<EventParticipant> eps){

            return EventDto.builder()
                    .eventId(event.getId())
                    .title(event.getTitle())
                    .description(event.getDescription())
                    .dtStartTime(event.getDtStartTime())
                    .dtEndTime(event.getDtEndTime())
                    .isAllDay(event.getIsAllDay())
                    .privacyType(event.getPrivacy())
                    .availability(event.getAvailability())
                    .location(event.getLocation())
                    .participants(eps.stream().map(ep -> ep.getUser().getId()).toList())
                    .build();
        }

        public static EventDto from(Event event){
            return EventDto.builder()
                    .eventId(event.getId())
                    .title(event.getTitle())
                    .description(event.getDescription())
                    .dtStartTime(event.getDtStartTime())
                    .dtEndTime(event.getDtEndTime())
                    .isAllDay(event.getIsAllDay())
                    .privacyType(event.getPrivacy())
                    .availability(event.getAvailability())
                    .location(event.getLocation())
                    .build();
        }
    }
}
