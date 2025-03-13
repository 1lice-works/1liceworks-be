package com.elice.iliceworksbe.team.repository;

import com.elice.iliceworksbe.auth.entity.User;
import com.elice.iliceworksbe.team.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    @Query("select e from Employee e join fetch e.userType join fetch e.position join fetch e.jobTitle where e.user = :user")
    Optional<Employee> findEmployeeByUser(User user);
}
