package com.elice.iliceworksbe.ai.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FindFreeTimeRequestDto {
    @JsonProperty("date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    @JsonProperty("userIds")
    private List<Long> userIds;

    @JsonProperty("duration")
    private int duration;
}
