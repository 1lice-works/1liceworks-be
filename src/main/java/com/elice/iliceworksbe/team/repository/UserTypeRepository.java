package com.elice.iliceworksbe.team.repository;

import com.elice.iliceworksbe.team.entity.UserType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserTypeRepository extends JpaRepository<UserType, Long> {
    Optional<UserType> findByName(String name);
    boolean existsByName(String name);
}
