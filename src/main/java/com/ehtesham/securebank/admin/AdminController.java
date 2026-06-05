package com.ehtesham.securebank.admin;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminController {
    @GetMapping("/api/v1/admin/test")
//    @PreAuthorize("hasRole('ADMIN')")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String adminTest() {

        System.out.println(
                org.springframework.security.core.context
                        .SecurityContextHolder
                        .getContext()
                        .getAuthentication()
        );

        return "Welcome Admin";
    }
}