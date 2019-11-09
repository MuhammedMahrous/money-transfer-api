package com.fintech.repository;

import com.fintech.model.Account;

import java.sql.SQLException;

public interface AccountRepository {
    Account getAccountById(Integer getAccountId) throws SQLException;

    boolean updateBalance(Account account) throws SQLException;
}
