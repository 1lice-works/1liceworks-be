package com.elice.iliceworksbe.ai.dto;

import com.elice.iliceworksbe.calendar.dto.response.EventJsonResponseDto;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FindFreeTimeInputDto {
    @JsonProperty("duration")
    private int duration ;

    @JsonProperty("date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    @JsonProperty("events")
    private List<EventJsonResponseDto> events;

    public static FindFreeTimeInputDto of(int duration, LocalDate date ,List<EventJsonResponseDto> events) {
        return FindFreeTimeInputDto.builder()
                .duration(duration)
                .date(date)
                .events(events.stream().collect(Collectors.toList()))
                .build();
    }
}
