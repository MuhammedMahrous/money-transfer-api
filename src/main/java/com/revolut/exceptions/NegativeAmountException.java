package com.revolut.exceptions;

public class NegativeAmountException extends RuntimeException {
    public NegativeAmountException() {
        super("Can't transfer negative amount");
    }
}
