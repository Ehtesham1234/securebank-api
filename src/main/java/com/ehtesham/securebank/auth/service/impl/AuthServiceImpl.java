package com.ehtesham.securebank.auth.service.impl;

import com.ehtesham.securebank.auth.dto.*;
import com.ehtesham.securebank.auth.entity.RefreshToken;
import com.ehtesham.securebank.auth.service.AuthService;
import com.ehtesham.securebank.auth.service.OtpService;
import com.ehtesham.securebank.auth.service.RefreshTokenService;
import com.ehtesham.securebank.common.enums.Role;
import com.ehtesham.securebank.common.enums.UserStatus;
import com.ehtesham.securebank.common.exception.EmailAlreadyExistsException;
import com.ehtesham.securebank.common.exception.InvalidCredentialsException;
import com.ehtesham.securebank.common.exception.InvalidOtpException;
import com.ehtesham.securebank.notification.EmailService;
import com.ehtesham.securebank.security.service.JwtService;
import com.ehtesham.securebank.user.dto.UserResponse;
import com.ehtesham.securebank.user.entity.User;
import com.ehtesham.securebank.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {


    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final OtpService otpService;
    private final EmailService emailService;
    public AuthServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            RefreshTokenService refreshTokenService, OtpService otpService, EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.otpService = otpService;
        this.emailService = emailService;
    }

    @Override
    public UserResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists");
        }

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhoneNumber(request.getPhoneNumber());
        user.setRole(Role.CUSTOMER);
        user.setUserStatus(UserStatus.PENDING_KYC);

        User savedUser = userRepository.save(user);

        UserResponse response = new UserResponse();
        response.setId(savedUser.getId());
        response.setFirstName(savedUser.getFirstName());
        response.setLastName(savedUser.getLastName());
        response.setEmail(savedUser.getEmail());
        response.setRole(savedUser.getRole());
        response.setUserStatus(savedUser.getUserStatus());

        return response;
    }

    @Override
    public AuthResponse login(LoginRequest request) {

        User user = userRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new InvalidCredentialsException(
                                "Invalid email or password"));

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword())) {
            throw new InvalidCredentialsException(
                    "Invalid email or password");
        }

        String accessToken = jwtService.generateToken(user.getEmail());
        RefreshToken refreshToken =
                refreshTokenService.createRefreshToken(user);

        return new AuthResponse(
                accessToken,
                refreshToken.getToken(),
                user.getUserStatus(),
                user.getRole()
        );
    }

    @Override
    @Transactional
    public AuthResponse refresh(RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenService
                .verifyRefreshToken(request.getRefreshToken());

        String newAccessToken = jwtService
                .generateToken(refreshToken.getUser().getEmail());

        return new AuthResponse(newAccessToken, refreshToken.getToken() , null , null);
    }

    @Override
    @Transactional
    public void logout(RefreshTokenRequest request) {
        refreshTokenService.revokeByToken(request.getRefreshToken());
    }

    @Override
    public void forgotPassword(ForgotPasswordRequest request) {

        // always return success even if email doesn't exist
        // security: don't reveal which emails are registered
        boolean userExists = userRepository
                .existsByEmail(request.getEmail());

        if (userExists) {
            String otp = otpService
                    .generateAndSaveOtp(request.getEmail());
            emailService.sendOtpEmail(request.getEmail(), otp);
        }

        // if user doesn't exist, we do nothing but still return success
        // caller never knows if email was registered or not
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {

        // 1. verify user exists
        User user = userRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new InvalidOtpException("Invalid or expired OTP"));
        // same error — don't reveal email existence

        // 2. verify OTP
        otpService.verifyOtp(request.getEmail(), request.getOtp());

        // 3. update password
        user.setPassword(
                passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        // 4. invalidate OTP so it can't be reused
        otpService.invalidateOtps(request.getEmail());

        // 5. revoke all refresh tokens — force re-login on all devices
        refreshTokenService.revokeAllUserTokens(user);
    }
}
