package com.elice.iliceworksbe.team.repository;

import com.elice.iliceworksbe.auth.entity.User;
import com.elice.iliceworksbe.team.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findByUser(User user);
}
