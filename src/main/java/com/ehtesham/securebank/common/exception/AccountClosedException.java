package com.ehtesham.securebank.common.exception;

import org.springframework.security.core.AuthenticationException;

public class AccountClosedException
        extends AuthenticationException {
    public AccountClosedException(String message) {
        super(message);
    }
}