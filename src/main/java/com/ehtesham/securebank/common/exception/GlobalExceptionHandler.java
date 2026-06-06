package com.ehtesham.securebank.common.exception;

import com.ehtesham.securebank.common.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<Map<String, String>> handleValidationException(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        errors.put(
                                error.getField(),
                                error.getDefaultMessage()
                        ));

        return ApiResponse.error("Validation failed", errors);
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ApiResponse<Void> handleEmailAlreadyExists(
            EmailAlreadyExistsException ex) {
        return ApiResponse.error(ex.getMessage());
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(InvalidCredentialsException.class)
    public ApiResponse<Void> handleInvalidCredentials(
            InvalidCredentialsException ex) {
        return ApiResponse.error(ex.getMessage());
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(TokenExpiredException.class)
    public ApiResponse<Void> handleTokenExpired(
            TokenExpiredException ex) {
        return ApiResponse.error(ex.getMessage());
    }
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidOtpException.class)
    public ApiResponse<Void> handleInvalidOtp(
            InvalidOtpException ex) {
        return ApiResponse.error(ex.getMessage());
    }
}