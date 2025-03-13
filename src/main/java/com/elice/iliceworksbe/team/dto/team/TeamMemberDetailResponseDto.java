package com.elice.iliceworksbe.team.dto.team;


import com.elice.iliceworksbe.auth.entity.User;
import com.elice.iliceworksbe.team.entity.Employee;
import lombok.Builder;

import java.time.format.DateTimeFormatter;
@Builder
public record TeamMemberDetailResponseDto(
        String userName,
        String accountId,
        String privateEmail,
        String phone,
        String jobTitle,
        String position,
        String userType,
        String responsibility,
        String employeeNumber,
        String hireDate

) {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd.");

    public static TeamMemberDetailResponseDto of(User user, Employee employee) {
        String hireDate = (employee.getHireDate() != null) ? employee.getHireDate().format(DATE_FORMATTER) : null;

        return TeamMemberDetailResponseDto.builder()
                .userName(user.getUsername())
                .accountId(user.getAccountId())
                .privateEmail(user.getPrivateEmail())
                .phone(user.getPhone())
                .jobTitle(employee.getJobTitle().getName())
                .position(employee.getPosition().getName())
                .userType(employee.getUserType().getName())
                .responsibility(employee.getResponsibility())
                .employeeNumber(employee.getEmployeeNumber())
                .hireDate(hireDate)
                .build();
    }
}
