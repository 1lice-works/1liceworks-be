package com.elice.iliceworksbe.team.dto.userType;

import com.elice.iliceworksbe.team.entity.UserType;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserTypeRequestDto {
    private String name;

    public UserType from() {
        return UserType.builder()
                .name(this.getName())
                .build();
    }
}
