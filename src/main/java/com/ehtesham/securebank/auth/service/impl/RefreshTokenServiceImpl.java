package com.ehtesham.securebank.auth.service.impl;

import com.ehtesham.securebank.auth.entity.RefreshToken;
import com.ehtesham.securebank.auth.repository.RefreshTokenRepository;
import com.ehtesham.securebank.auth.service.RefreshTokenService;
import com.ehtesham.securebank.common.exception.TokenExpiredException;
import com.ehtesham.securebank.user.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    public RefreshTokenServiceImpl(
            RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Override
    @Transactional
    public RefreshToken createRefreshToken(User user) {
        // revoke old tokens first — one active refresh token per user
        refreshTokenRepository.revokeAllUserTokens(user);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setUser(user);
        refreshToken.setExpiryDate(
                Instant.now().plusMillis(refreshExpiration));
        refreshToken.setRevoked(false);

        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    @Transactional(readOnly = true)
    public RefreshToken verifyRefreshToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository
                .findByToken(token)
                .orElseThrow(() ->
                        new TokenExpiredException("Invalid refresh token"));

        if (refreshToken.isRevoked()) {
            throw new TokenExpiredException("Refresh token has been revoked");
        }

        if (refreshToken.getExpiryDate().isBefore(Instant.now())) {
            throw new TokenExpiredException("Refresh token has expired");
        }

        return refreshToken;
    }

    @Override
    @Transactional
    public void revokeAllUserTokens(User user) {
        refreshTokenRepository.revokeAllUserTokens(user);
    }

    @Override
    @Transactional
    public void revokeByToken(String token) {
        refreshTokenRepository
                .findByToken(token)
                .ifPresent(refreshToken ->
                        refreshTokenRepository
                                .revokeAllUserTokens(refreshToken.getUser()));
    }
}