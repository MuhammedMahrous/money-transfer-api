package com.revolut.service.impl;

import com.revolut.model.Account;
import com.revolut.repository.AccountRepository;
import com.revolut.service.AccountService;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import java.sql.SQLException;

@ApplicationScoped
@Slf4j
public class AccountServiceImpl implements AccountService {

    private AccountRepository accountRepository;

    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public Account getAccountById(Integer getAccountId) {
        Account account = null;
        try {
            account = accountRepository.getAccountById(getAccountId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return account;
    }

    @Override
    public boolean updateBalance(Account account) {
        boolean updated = false;
        try {
            updated = accountRepository.updateBalance(account);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return updated;
    }
}
