package com.elice.iliceworksbe.team.web;

import com.elice.iliceworksbe.auth.model.UserDetailsImpl;
import com.elice.iliceworksbe.common.exception.BaseResponse;
import com.elice.iliceworksbe.common.exception.ErrorCode;
import com.elice.iliceworksbe.team.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/employee")
@RequiredArgsConstructor
@Tag(name = "Employee", description = "직원 관련 API 입니다.")

public class EmployeeController {

    private final EmployeeService employeeService;

    @Operation(summary = "직원 삭제", description = "(개발 완료 x)팀장이 유저를 삭제하기 전에 실행해야 하는 메서드입니다.")
    @PreAuthorize("hasAuthority('LEADER')")
    @DeleteMapping("/{employeeId}")
    public BaseResponse<String> deleteEmployee(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long employeeId) {
        employeeService.deleteEmployee(employeeId);
        return new BaseResponse<>(ErrorCode.NO_CONTENT);
    }
}
