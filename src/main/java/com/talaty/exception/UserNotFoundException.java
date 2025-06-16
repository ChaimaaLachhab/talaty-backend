package com.talaty.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException() {
        super("Invalid credentials");
    }
}
