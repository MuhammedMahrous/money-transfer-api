package com.revolut.exceptions;

public class NoEnoughBalanceException extends RuntimeException {
    public NoEnoughBalanceException(Integer accountId) {
        super("Account with id [" + accountId + "] doesn't have enough balance to make the transaction");
    }
}
