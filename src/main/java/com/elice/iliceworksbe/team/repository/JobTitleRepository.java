package com.elice.iliceworksbe.team.repository;

import com.elice.iliceworksbe.team.entity.JobTitle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JobTitleRepository extends JpaRepository<JobTitle, Long> {
    Optional<JobTitle> findByName(String name);
    boolean existsByName(String name);
}
