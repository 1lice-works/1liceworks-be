package com.elice.iliceworksbe.team.entity;

import com.elice.iliceworksbe.common.entity.BaseEntity;
import com.elice.iliceworksbe.team.constant.Industry;
import com.elice.iliceworksbe.team.constant.Scale;
import com.elice.iliceworksbe.team.dto.team.TeamInfoUpdateDto;
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
@Table(name = "TEAM")
@AuditOverride(forClass = BaseEntity.class)
public class Team extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_id", nullable = false)
    private Long id;

    @Column(name = "company_name", nullable = false)
    private String companyName;

    @Column(name = "team_name", nullable = false)
    private String teamName;

    @Column(name = "domain_name", nullable = false)
    private String domainName;

    @Column(name = "has_private_domain", nullable = false)
    private Boolean hasPrivateDomain;

    @Column(name = "industry", nullable = false)
    @Enumerated(EnumType.STRING)
    private Industry industry;

    @Column(name = "scale", nullable = false)
    @Enumerated(EnumType.STRING)
    private Scale scale;

    public void updateTeamInfo(TeamInfoUpdateDto teamInfoUpdateDto) {
        this.teamName = teamInfoUpdateDto.teamName();
        this.domainName = teamInfoUpdateDto.domainName();
    }
}
