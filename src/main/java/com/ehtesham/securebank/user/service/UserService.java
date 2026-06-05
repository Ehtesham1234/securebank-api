package com.ehtesham.securebank.user.service;

import com.ehtesham.securebank.security.dto.AuthResponse;
import com.ehtesham.securebank.security.dto.LoginRequest;
import com.ehtesham.securebank.user.dto.RegisterRequest;
import com.ehtesham.securebank.user.dto.UserResponse;

public interface UserService {
    UserResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}
