package com.intuitech.cvprocessor.feature.auth;

/**
 * Thrown when authentication fails (invalid credentials, account disabled/locked).
 */
public class AuthenticationException extends Exception {

    public AuthenticationException(String message) {
        super(message);
    }
}
