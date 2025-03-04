package com.elice.iliceworksbe.calendar.dto.response;

import com.elice.iliceworksbe.calendar.entity.Event;
import com.elice.iliceworksbe.common.constant.Availability;
import com.elice.iliceworksbe.common.constant.PrivacyType;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record GetEventsByTitleKeywordResponseDto(
    int count,
    List<EventDto> events

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
            String location
    ){
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

    public static GetEventsByTitleKeywordResponseDto from(List<Event> resultEvents) {
        return GetEventsByTitleKeywordResponseDto.builder()
                .count(resultEvents.size())
                .events(resultEvents.stream()
                        .map(EventDto::from)
                        .toList())
                .build();
    }
}
