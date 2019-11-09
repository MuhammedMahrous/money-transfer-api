package com.fintech.repository;

import com.fintech.model.MoneyTransfer;

import java.sql.SQLException;

public interface MoneyTransferRepository {
    MoneyTransfer getById(Integer id) throws SQLException;

    MoneyTransfer create(MoneyTransfer moneyTransfer) throws SQLException;
}
