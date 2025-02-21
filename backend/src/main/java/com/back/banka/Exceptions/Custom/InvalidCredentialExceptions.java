package com.back.banka.Exceptions.Custom;

public class InvalidCredentialExceptions extends RuntimeException {
    public InvalidCredentialExceptions(String message) {
        super(message);
    }
}
