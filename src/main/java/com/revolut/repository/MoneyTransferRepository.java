package com.revolut.repository;

import com.revolut.model.MoneyTransfer;

import java.sql.SQLException;

public interface MoneyTransferRepository {
    MoneyTransfer getById(Integer id) throws SQLException;

    MoneyTransfer create(MoneyTransfer moneyTransfer) throws SQLException;
}
