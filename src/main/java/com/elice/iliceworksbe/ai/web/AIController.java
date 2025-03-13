package com.elice.iliceworksbe.ai.web;

import com.elice.iliceworksbe.ai.dto.FindFreeTimeRequestDto;
import com.elice.iliceworksbe.ai.dto.FindFreeTimeResponseDto;
import com.elice.iliceworksbe.ai.dto.GenerateScheduleRequestDto;
import com.elice.iliceworksbe.ai.dto.GenerateScheduleResponseDto;
import com.elice.iliceworksbe.ai.service.AIService;
import com.elice.iliceworksbe.auth.model.UserDetailsImpl;
import com.elice.iliceworksbe.calendar.service.EventService;
import com.elice.iliceworksbe.common.exception.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ai")
@Tag(name = "AI", description = "AI 기능 관련 API 입니다.")
public class AIController {

    private final AIService aiService;
    private final EventService eventService;

    @Operation(summary = "AI 일정 생성 기능", description = "프롬프트에 값을 입력하면 AI가 캘린더 일정 생성 JSON 데이터를 반환합니다.")
    @PostMapping("/generate_schedule")
    public BaseResponse<GenerateScheduleResponseDto> generateSchedule(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody GenerateScheduleRequestDto requestDto) {
        GenerateScheduleResponseDto result = aiService.generateSchedule(requestDto);
        return new BaseResponse<>(result);
    }

    @Operation(summary = "AI 빈 시간 찾기 기능", description = "AI가 일정 중 빈 시간을 찾아서 JSON 데이터로 반환합니다.")
    @PostMapping("find_free_time")
    public BaseResponse<FindFreeTimeResponseDto> findFreeTime(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam Long teamCalendarId,
            @RequestBody FindFreeTimeRequestDto requestDto) {
        FindFreeTimeResponseDto result = aiService.findFreeTime(teamCalendarId, requestDto);
        return new BaseResponse<>(result);
    }
}
