package com.elice.iliceworksbe.team.entity;

import com.elice.iliceworksbe.common.entity.BaseEntity;
import com.elice.iliceworksbe.team.dto.jobTitle.JobTitleRequestDto;
import com.elice.iliceworksbe.team.dto.jobTitle.JobTitleUpdateDto;
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
@Table(name = "JOB_TITLE")
@AuditOverride(forClass = BaseEntity.class)
public class JobTitle extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "job_title_id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    public void update(JobTitleUpdateDto jobTitleUpdateDto) {
        this.name = jobTitleUpdateDto.name();
    }

    public static JobTitle from(JobTitleRequestDto requestDto) {
        return JobTitle.builder()
                .name(requestDto.name())
                .build();
    }
}
