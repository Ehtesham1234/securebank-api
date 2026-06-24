package com.ehtesham.securebank.auth.service;

import com.ehtesham.securebank.auth.dto.*;
import com.ehtesham.securebank.user.dto.UserResponse;

public interface AuthService {
    UserResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    AuthResponse refresh(RefreshTokenRequest request);
    void logout(RefreshTokenRequest request);
    void forgotPassword(ForgotPasswordRequest request);    // ← new
    void resetPassword(ResetPasswordRequest request);

    UserResponse createStaffUser(CreateStaffRequest request);// ← new
}
