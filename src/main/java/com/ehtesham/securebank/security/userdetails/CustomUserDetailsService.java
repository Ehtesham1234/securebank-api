package com.ehtesham.securebank.security.userdetails;

import com.ehtesham.securebank.user.entity.User;
import com.ehtesham.securebank.user.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(
            UserRepository userRepository) {

        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(
            String email)
            throws UsernameNotFoundException {

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "User not found"));

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                java.util.List.of(
                        new org.springframework.security.core.authority.SimpleGrantedAuthority(
                                "ROLE_" + user.getRole().name()
                        )
                )
        );
    }
}