package com.ehtesham.securebank.auth.service.impl;

import com.ehtesham.securebank.auth.entity.PasswordResetOtp;
import com.ehtesham.securebank.auth.repository.PasswordResetOtpRepository;
import com.ehtesham.securebank.auth.service.OtpService;
import com.ehtesham.securebank.common.exception.InvalidOtpException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Random;

@Service
public class OtpServiceImpl implements OtpService {

    private static final long OTP_EXPIRY_MINUTES = 10;
    private final PasswordResetOtpRepository otpRepository;

    public OtpServiceImpl(PasswordResetOtpRepository otpRepository) {
        this.otpRepository = otpRepository;
    }

    @Override
    @Transactional
    public String generateAndSaveOtp(String email) {

        // invalidate any existing OTPs for this email
        otpRepository.invalidateAllOtps(email);

        String otp = String.format("%06d",
                new Random().nextInt(999999));

        PasswordResetOtp entity = new PasswordResetOtp();
        entity.setEmail(email);
        entity.setOtp(otp);
        entity.setExpiryDate(
                Instant.now().plusSeconds(OTP_EXPIRY_MINUTES * 60));
        entity.setUsed(false);

        otpRepository.save(entity);
        return otp;
    }

    @Override
    @Transactional(readOnly = true)
    public void verifyOtp(String email, String otp) {

        PasswordResetOtp entity = otpRepository
                .findByEmailAndOtpAndUsedFalse(email, otp)
                .orElseThrow(() ->
                        new InvalidOtpException("Invalid or expired OTP"));

        if (entity.getExpiryDate().isBefore(Instant.now())) {
            throw new InvalidOtpException("OTP has expired");
        }
    }

    @Override
    @Transactional
    public void invalidateOtps(String email) {
        otpRepository.invalidateAllOtps(email);
    }
}