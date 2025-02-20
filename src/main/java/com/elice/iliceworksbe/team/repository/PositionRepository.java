package com.elice.iliceworksbe.team.repository;

import com.elice.iliceworksbe.team.entity.Position;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PositionRepository extends JpaRepository<Position, Long> {
    Optional<Position> findByName(String name);
    boolean existsByName(String name);
}
