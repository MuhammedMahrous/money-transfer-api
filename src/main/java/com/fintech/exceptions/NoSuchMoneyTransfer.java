package com.fintech.exceptions;

public class NoSuchMoneyTransfer extends RuntimeException {

    public NoSuchMoneyTransfer(Integer moneyTransferId) {
        super("No MoneyTransfer with id [" + moneyTransferId + "]");
    }
}
