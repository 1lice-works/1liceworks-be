package com.elice.iliceworksbe.team.entity;

import com.elice.iliceworksbe.auth.dto.request.PatchMemberProfileRequestDto;
import com.elice.iliceworksbe.auth.entity.User;
import com.elice.iliceworksbe.common.entity.BaseEntity;
import com.elice.iliceworksbe.team.dto.team.TeamMemberInfoUpdateDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.envers.AuditOverride;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "EMPLOYEE")
@AuditOverride(forClass = BaseEntity.class)
public class Employee extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "employee_id", nullable = false)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_type_id", nullable = false)
    private UserType userType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "position_id", nullable = false)
    private Position position;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_title_id", nullable = false)
    private JobTitle jobTitle;

    @Column(name = "responsibility")
    private String responsibility;

    @Column(name = "employee_number")
    private String employeeNumber;

    @CreatedDate
    @Column(name = "hire_date")
    private LocalDateTime hireDate;

    public void patchEmployeeInfo(
            TeamMemberInfoUpdateDto teamMemberInfoUpdateDto,
            JobTitle jobTitle, Position position, UserType userType) {
        this.jobTitle = jobTitle;
        this.position = position;
        this.userType = userType;
        this.responsibility = teamMemberInfoUpdateDto.responsibility();
        this.employeeNumber = teamMemberInfoUpdateDto.employeeNumber();
    }

    public void designateResponsibility(String responsibility) {
        this.responsibility = responsibility;
    }

    public void patchProfile(PatchMemberProfileRequestDto patchProfileRequestDto, Position patchedPosition, JobTitle patchedJobTitle, UserType patchedUserType) {
        this.responsibility = patchProfileRequestDto.responsibility();
        this.employeeNumber = patchProfileRequestDto.employeeNumber();
        this.jobTitle = patchedJobTitle;
        this.position = patchedPosition;
        this.userType = patchedUserType;
    }
}
