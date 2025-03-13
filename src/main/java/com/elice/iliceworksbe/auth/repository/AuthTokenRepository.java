package com.elice.iliceworksbe.auth.repository;

import com.elice.iliceworksbe.auth.entity.AuthToken;
import com.elice.iliceworksbe.auth.entity.User;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthTokenRepository extends JpaRepository<AuthToken, Long> {

    @Query("SELECT a FROM AuthToken a JOIN FETCH a.user WHERE a.refreshToken = :token")
    Optional<AuthToken> findByTokenWithUser(@Param("token") String token);
    Optional<AuthToken> findAuthTokenByUser(User user);

}
