package com.revolut.repository;

import com.revolut.model.Account;

import java.sql.SQLException;

public interface AccountRepository {
    Account getAccountById(Integer getAccountId) throws SQLException;

    boolean updateBalance(Account account) throws SQLException;
}
