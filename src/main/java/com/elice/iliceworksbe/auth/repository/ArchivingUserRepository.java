package com.elice.iliceworksbe.auth.repository;

import com.elice.iliceworksbe.auth.entity.ArchivingUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArchivingUserRepository extends JpaRepository<ArchivingUser, Long> {
}
