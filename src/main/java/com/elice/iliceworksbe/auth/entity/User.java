package com.elice.iliceworksbe.auth.entity;

import com.elice.iliceworksbe.auth.dto.request.PatchProfileRequestDto;
import com.elice.iliceworksbe.common.constant.Role;
import com.elice.iliceworksbe.common.constant.Status;
import com.elice.iliceworksbe.common.entity.BaseEntity;
import com.elice.iliceworksbe.team.entity.Team;
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
@Table(name = "USERS")
@AuditOverride(forClass = BaseEntity.class)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false)
    private Long id;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "account_id", nullable = false)
    private String accountId;

    @Column(name = "private_email")
    private String privateEmail;

    @Column(name = "role", nullable = false)
    private Role role;

    @Column(name = "profile_image")
    private String profileImage;

    @Column(name = "phone")
    private String phone;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "is_team_created")
    private Boolean isTeamCreated;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    public void patchMyProfile(PatchProfileRequestDto patchProfileRequestDto, String updatedProfileImageUrl) {
        this.username = patchProfileRequestDto.username();
        this.phone = patchProfileRequestDto.phone();
        this.profileImage = updatedProfileImageUrl;
    }

    public void patchUsername(String username) {
        this.username = username;
    }

    public void setUserStatus(Status status) {
        this.status = status;
    }

    public ArchivingUser toArchivingUser() {
        return ArchivingUser.builder()
                .username(this.username)
                .password(this.password)
                .accountId(this.accountId)
                .privateEmail(this.privateEmail)
                .role(this.role)
                .profileImage(this.profileImage)
                .phone(this.phone)
                .isTeamCreated(this.isTeamCreated)
                .team(this.team)
                .build();
    }
}
