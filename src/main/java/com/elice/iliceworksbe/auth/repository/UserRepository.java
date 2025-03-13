package com.elice.iliceworksbe.auth.repository;

import com.elice.iliceworksbe.auth.entity.User;
import com.elice.iliceworksbe.team.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByAccountId(String accountId);
    List<User> findByTeam(Team team);
    Boolean existsByPrivateEmail(String privateEmail);
    Boolean existsByAccountId(String accountId);
    @Query("SELECT u.team.id FROM User u WHERE u.id = :userId")
    Optional<Long> findTeamIdByUserId(@Param("userId") Long userId);
}
