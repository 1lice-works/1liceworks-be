package com.elice.iliceworksbe.team.entity;

import com.elice.iliceworksbe.common.entity.BaseEntity;
import com.elice.iliceworksbe.team.dto.userType.UserTypeRequestDto;
import com.elice.iliceworksbe.team.dto.userType.UserTypeUpdateDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.envers.AuditOverride;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "USER_TYPE")
@AuditOverride(forClass = BaseEntity.class)
public class UserType extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_type_id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    public void update(UserTypeUpdateDto userTypeUpdateDto) {
        this.name = userTypeUpdateDto.name();
    }

    public static UserType from(UserTypeRequestDto requestDto) {
        return UserType.builder()
                .name(requestDto.name())
                .build();
    }
}
