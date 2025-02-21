package com.elice.iliceworksbe.calendar.web;

import com.elice.iliceworksbe.auth.model.UserDetailsImpl;
import com.elice.iliceworksbe.calendar.dto.request.PostTeamEventRequestDto;
import com.elice.iliceworksbe.calendar.service.EventService;
import com.elice.iliceworksbe.common.exception.BaseResponse;
import com.elice.iliceworksbe.common.exception.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequestMapping("/api/calendar")
@RequiredArgsConstructor
@Tag(name = "Calendar", description = "캘린더 기능 관련 API 입니다.")
public class CalendarController {

    private final EventService eventService;

    @Operation(summary = "팀 캘린더 일정 생성", description = "해당하는 팀 캘린더ID에서 일정을 생성합니다.")
    @PostMapping("/team-events")
    public BaseResponse<Void> postTeamEvent(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestParam Long calendarId,
                                            @RequestBody PostTeamEventRequestDto postTeamEventRequestDto) {

        eventService.postTeamEvent(userDetails.getUserId(), calendarId, postTeamEventRequestDto);
        return new BaseResponse<>(ErrorCode.NO_CONTENT);
    }
}
