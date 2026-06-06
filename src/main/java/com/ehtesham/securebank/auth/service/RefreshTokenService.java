package com.ehtesham.securebank.auth.service;

import com.ehtesham.securebank.auth.entity.RefreshToken;
import com.ehtesham.securebank.user.entity.User;

public interface RefreshTokenService {
    RefreshToken createRefreshToken(User user);
    RefreshToken verifyRefreshToken(String token);
    void revokeAllUserTokens(User user);
    void revokeByToken(String token);
}