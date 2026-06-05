package com.ehtesham.securebank.user.controller;

import com.ehtesham.securebank.user.dto.RegisterRequest;
import com.ehtesham.securebank.user.dto.UserResponse;
import com.ehtesham.securebank.user.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
   private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public UserResponse register(@Valid @RequestBody RegisterRequest request){
        return userService.register(request);
    }
}
