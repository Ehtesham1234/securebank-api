package com.ehtesham.securebank.user.service;

import com.ehtesham.securebank.user.dto.RegisterRequest;
import com.ehtesham.securebank.user.dto.UserResponse;

public interface UserService {
    UserResponse register(RegisterRequest request);
}
