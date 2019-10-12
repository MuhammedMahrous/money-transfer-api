package com.revolut.service;

import com.revolut.model.MoneyTransfer;

public interface MoneyTransferService {
    MoneyTransfer transferMoney(MoneyTransfer moneyTransfer);
}
