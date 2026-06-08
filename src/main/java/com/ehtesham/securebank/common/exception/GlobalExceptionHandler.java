package com.ehtesham.securebank.common.exception;

import com.ehtesham.securebank.common.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ── Validation ──────────────────────────────────────────────

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse handleValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        Map<String, String> errors = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errors.put(fieldError.getField(),
                    fieldError.getDefaultMessage());
        }

        return ErrorResponse.validation(
                "Input validation failed",
                request.getRequestURI(),
                errors);
    }

    // ── Auth exceptions ─────────────────────────────────────────

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ErrorResponse handleEmailAlreadyExists(
            EmailAlreadyExistsException ex,
            HttpServletRequest request) {

        return ErrorResponse.of(
                HttpStatus.CONFLICT.value(),
                "CONFLICT",
                ex.getMessage(),
                request.getRequestURI());
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(InvalidCredentialsException.class)
    public ErrorResponse handleInvalidCredentials(
            InvalidCredentialsException ex,
            HttpServletRequest request) {

        return ErrorResponse.of(
                HttpStatus.UNAUTHORIZED.value(),
                "UNAUTHORIZED",
                ex.getMessage(),
                request.getRequestURI());
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(TokenExpiredException.class)
    public ErrorResponse handleTokenExpired(
            TokenExpiredException ex,
            HttpServletRequest request) {

        return ErrorResponse.of(
                HttpStatus.UNAUTHORIZED.value(),
                "TOKEN_EXPIRED",
                ex.getMessage(),
                request.getRequestURI());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidOtpException.class)
    public ErrorResponse handleInvalidOtp(
            InvalidOtpException ex,
            HttpServletRequest request) {

        return ErrorResponse.of(
                HttpStatus.BAD_REQUEST.value(),
                "INVALID_OTP",
                ex.getMessage(),
                request.getRequestURI());
    }

    // ── Account status exceptions ────────────────────────────────

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(KycNotVerifiedException.class)
    public ErrorResponse handleKycNotVerified(
            KycNotVerifiedException ex,
            HttpServletRequest request) {

        return ErrorResponse.of(
                HttpStatus.FORBIDDEN.value(),
                "KYC_NOT_VERIFIED",
                ex.getMessage(),
                request.getRequestURI());
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(AccountSuspendedException.class)
    public ErrorResponse handleAccountSuspended(
            AccountSuspendedException ex,
            HttpServletRequest request) {

        return ErrorResponse.of(
                HttpStatus.FORBIDDEN.value(),
                "ACCOUNT_SUSPENDED",
                ex.getMessage(),
                request.getRequestURI());
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(AccountClosedException.class)
    public ErrorResponse handleAccountClosed(
            AccountClosedException ex,
            HttpServletRequest request) {

        return ErrorResponse.of(
                HttpStatus.FORBIDDEN.value(),
                "ACCOUNT_CLOSED",
                ex.getMessage(),
                request.getRequestURI());
    }

    // ── Spring Security exceptions ───────────────────────────────

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(LockedException.class)
    public ErrorResponse handleLocked(
            LockedException ex,
            HttpServletRequest request) {

        return ErrorResponse.of(
                HttpStatus.FORBIDDEN.value(),
                "ACCOUNT_LOCKED",
                ex.getMessage(),
                request.getRequestURI());
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(DisabledException.class)
    public ErrorResponse handleDisabled(
            DisabledException ex,
            HttpServletRequest request) {

        return ErrorResponse.of(
                HttpStatus.FORBIDDEN.value(),
                "ACCOUNT_DISABLED",
                ex.getMessage(),
                request.getRequestURI());
    }

    // ── Generic fallback ─────────────────────────────────────────

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ErrorResponse handleGenericException(
            Exception ex,
            HttpServletRequest request) {

        return ErrorResponse.of(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "INTERNAL_SERVER_ERROR",
                "An unexpected error occurred",
                request.getRequestURI());
    }
}