package com.elice.iliceworksbe.auth.dto.response;

import com.elice.iliceworksbe.auth.entity.User;
import com.elice.iliceworksbe.common.constant.Role;
import lombok.Builder;

@Builder
public record GetMySemiProfileResponseDto(
        Long userId,
        String username,
        Role role,
        String profileImage
) {
    public static GetMySemiProfileResponseDto from(User user) {
        return GetMySemiProfileResponseDto.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .role(user.getRole())
                .profileImage(user.getProfileImage())
                .build();
    }
}
