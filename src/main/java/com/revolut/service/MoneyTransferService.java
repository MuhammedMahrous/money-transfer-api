package com.revolut.service;

import com.revolut.model.MoneyTransfer;

import java.sql.SQLException;

public interface MoneyTransferService {
    MoneyTransfer transferMoney(MoneyTransfer moneyTransfer) throws Exception;

    MoneyTransfer getById(Integer id) throws SQLException;

    MoneyTransfer create(MoneyTransfer moneyTransfer) throws SQLException;
}
