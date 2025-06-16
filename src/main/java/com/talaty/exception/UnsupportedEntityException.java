package com.talaty.exception;

public class UnsupportedEntityException extends IllegalArgumentException {
    public UnsupportedEntityException(String message) {
        super(message);
    }
}