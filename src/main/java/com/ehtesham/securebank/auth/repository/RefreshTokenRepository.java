package com.ehtesham.securebank.auth.repository;

import com.ehtesham.securebank.auth.entity.RefreshToken;
import com.ehtesham.securebank.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    // used during logout — revoke all tokens for a user
    @Modifying
    @Query("UPDATE RefreshToken rt SET rt.revoked = true WHERE rt.user = :user")
    void revokeAllUserTokens(User user);

    @Modifying
    @Query("UPDATE RefreshToken rt SET rt.revoked = true " +
            "WHERE rt.tokenFamily = :tokenFamily")
    void revokeByTokenFamily(String tokenFamily);
}