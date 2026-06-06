package com.ehtesham.securebank.auth.repository;

import com.ehtesham.securebank.auth.entity.PasswordResetOtp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PasswordResetOtpRepository
        extends JpaRepository<PasswordResetOtp, Long> {

    Optional<PasswordResetOtp> findByEmailAndOtpAndUsedFalse(
            String email, String otp);

    @Modifying
    @Query("UPDATE PasswordResetOtp p SET p.used = true WHERE p.email = :email")
    void invalidateAllOtps(String email);
}