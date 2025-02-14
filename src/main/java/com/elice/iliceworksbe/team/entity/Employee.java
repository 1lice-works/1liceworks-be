package com.elice.iliceworksbe.team.entity;

import com.elice.iliceworksbe.common.constant.Position;
import com.elice.iliceworksbe.common.constant.UserType;
import com.elice.iliceworksbe.common.entity.BaseEntity;
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

    @Column(name = "user_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private UserType userType;

    @Column(name = "job_title")
    private String jobTitle;

    @Column(name = "position", nullable = false)
    @Enumerated(EnumType.STRING)
    private Position position;

    @Column(name = "responsibility")
    private String responsibility;

    @Column(name = "employee_number")
    private String employeeNumber;

    @CreatedDate
    @Column(name = "hire_date")
    private LocalDateTime hireDate;

}
