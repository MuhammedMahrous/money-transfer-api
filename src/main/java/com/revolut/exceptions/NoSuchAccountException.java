package com.revolut.exceptions;

public class NoSuchAccountException extends RuntimeException {
    public NoSuchAccountException() {
    }

    public NoSuchAccountException(Integer accountId) {
        super("Account with id [" + accountId + "] doesn't exist");
    }

    public NoSuchAccountException(String message) {
        super(message);
    }
}
