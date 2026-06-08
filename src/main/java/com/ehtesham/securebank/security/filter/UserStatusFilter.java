package com.ehtesham.securebank.security.filter;

import com.ehtesham.securebank.common.enums.UserStatus;
import com.ehtesham.securebank.user.entity.User;
import com.ehtesham.securebank.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
public class UserStatusFilter extends OncePerRequestFilter {

    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    public UserStatusFilter(
            UserRepository userRepository,
            ObjectMapper objectMapper) {
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String requestURI = request.getRequestURI();

        // skip auth endpoints entirely
        if (requestURI.startsWith("/api/v1/auth/")) {
            filterChain.doFilter(request, response);
            return;
        }

        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        // skip if not authenticated
        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication.getPrincipal()
                .equals("anonymousUser")) {
            filterChain.doFilter(request, response);
            return;
        }

        String email = authentication.getName();
        User user = userRepository
                .findByEmail(email)
                .orElse(null);

        if (user == null) {
            filterChain.doFilter(request, response);
            return;
        }

        // PENDING_KYC → can only access KYC endpoints
        if (user.getUserStatus() == UserStatus.PENDING_KYC
                && !requestURI.startsWith("/api/v1/kyc/")) {
            writeErrorResponse(
                    response,
                    HttpServletResponse.SC_FORBIDDEN,
                    "KYC_NOT_VERIFIED",
                    "Please complete KYC verification first",
                    requestURI);
            return;
        }

        // SUSPENDED → block everything
        if (user.getUserStatus() == UserStatus.SUSPENDED) {
            writeErrorResponse(
                    response,
                    HttpServletResponse.SC_FORBIDDEN,
                    "ACCOUNT_SUSPENDED",
                    "Your account has been suspended. " +
                            "Please contact support.",
                    requestURI);
            return;
        }

        // CLOSED → block everything
        if (user.getUserStatus() == UserStatus.CLOSED) {
            writeErrorResponse(
                    response,
                    HttpServletResponse.SC_FORBIDDEN,
                    "ACCOUNT_CLOSED",
                    "This account has been closed.",
                    requestURI);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void writeErrorResponse(
            HttpServletResponse response,
            int status,
            String error,
            String message,
            String path) throws IOException {

        response.setStatus(status);
        response.setContentType("application/json");

        Map<String, Object> body = new HashMap<>();
        body.put("success", false);
        body.put("status", status);
        body.put("error", error);
        body.put("message", message);
        body.put("path", path);
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("validationErrors", null);

        response.getWriter()
                .write(objectMapper.writeValueAsString(body));
    }
}