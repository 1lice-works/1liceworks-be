package com.elice.iliceworksbe.ai.service;

import com.elice.iliceworksbe.ai.dto.FindFreeTimeRequestDto;
import com.elice.iliceworksbe.ai.dto.FindFreeTimeResponseDto;
import com.elice.iliceworksbe.ai.dto.GenerateScheduleRequestDto;
import com.elice.iliceworksbe.ai.dto.GenerateScheduleResponseDto;

import java.util.Map;

public interface AIService {
    GenerateScheduleResponseDto generateSchedule(GenerateScheduleRequestDto requestDto);
    FindFreeTimeResponseDto findFreeTime(Long teamCalendarId, FindFreeTimeRequestDto requestDto);
}
