package com.ehtesham.securebank.auth.service;

public interface OtpService {
    String generateAndSaveOtp(String email);
    void verifyOtp(String email, String otp);
    void invalidateOtps(String email);
}