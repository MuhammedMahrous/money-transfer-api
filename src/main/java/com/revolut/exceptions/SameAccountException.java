package com.revolut.exceptions;

public class SameAccountException extends RuntimeException {
    public SameAccountException() {
        super("Can't have sourceAccount same as targetAccount");
    }
}
