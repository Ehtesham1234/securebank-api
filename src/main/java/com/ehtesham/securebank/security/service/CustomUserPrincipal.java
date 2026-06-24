package com.ehtesham.securebank.security.service;

import com.ehtesham.securebank.common.enums.UserStatus;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
public class CustomUserPrincipal extends User {

    private final Long userId;
    private final UserStatus userStatus;

    public CustomUserPrincipal(
            Long userId,
            String email,
            String password,
            UserStatus userStatus,
            Collection<? extends GrantedAuthority> authorities) {
        super(email, password, authorities);
        this.userId = userId;
        this.userStatus = userStatus;
    }
}