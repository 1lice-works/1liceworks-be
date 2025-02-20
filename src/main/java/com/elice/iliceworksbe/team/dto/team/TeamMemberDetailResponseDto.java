package com.elice.iliceworksbe.team.dto.team;


import com.elice.iliceworksbe.auth.entity.User;
import com.elice.iliceworksbe.team.entity.Employee;

import java.time.format.DateTimeFormatter;

public record TeamMemberDetailResponseDto(
        String userName,
        String accountId,
        String privateEmail,
        String phone,
        String jobTitleName,
        String positionName,
        String userTypeName,
        String responsibility,
        String employeeNumber,
        String hireDate

) {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd.");



    public static TeamMemberDetailResponseDto of(User user, Employee employee) {
        String hireDate = (employee.getHireDate() != null) ? employee.getHireDate().format(DATE_FORMATTER) : null;

        return new TeamMemberDetailResponseDto(
                user.getUsername(),
                user.getAccountId(),
                user.getPrivateEmail(),
                user.getPhone(),
                employee.getJobTitle().getName(),
                employee.getPosition().getName(),
                employee.getUserType().getName(),
                employee.getResponsibility(),
                employee.getEmployeeNumber(),
                hireDate
        );
    }
}
