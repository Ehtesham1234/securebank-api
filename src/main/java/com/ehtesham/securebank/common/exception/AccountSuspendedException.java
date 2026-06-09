package com.ehtesham.securebank.common.exception;

import org.springframework.security.core.AuthenticationException;

public class AccountSuspendedException
        extends AuthenticationException {
    public AccountSuspendedException(String message) {
        super(message);
    }
}
