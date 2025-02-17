package com.elice.iliceworksbe.team.dto.userType;


import com.elice.iliceworksbe.team.entity.UserType;

public record UserTypeResponseDto(Long id, String name) {
    public static UserTypeResponseDto from(UserType userType) {
        return new UserTypeResponseDto(userType.getId(), userType.getName());
    }
}
