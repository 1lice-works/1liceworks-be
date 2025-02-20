package com.elice.iliceworksbe.auth.dto.response;

import com.elice.iliceworksbe.auth.entity.User;
import com.elice.iliceworksbe.team.entity.Employee;
import lombok.Builder;

import java.time.format.DateTimeFormatter;

@Builder
public record GetProfileResponseDto(
        String username,
        String accountId,
        String profileImage,
        String phone,
        String privateEmail,
        String userType,
        String position,
        String jobTitle,
        String responsibility,
        String employeeNumber,
        String hireDate
) {
    public static GetProfileResponseDto of(User user, Employee employee) {
        return GetProfileResponseDto.builder()
                .username(user.getUsername())
                .accountId(user.getAccountId())
                .profileImage(user.getProfileImage())
                .phone(user.getPhone())
                .privateEmail(user.getPrivateEmail())
                .userType(employee.getUserType().getName())
                .position(employee.getPosition().getName())
                .jobTitle(employee.getJobTitle().getName())
                .responsibility(employee.getResponsibility())
                .employeeNumber(employee.getEmployeeNumber())
                .hireDate(employee.getHireDate().format(DateTimeFormatter.ofPattern("yyyy. MM. dd")))
                .build();
    }

}
