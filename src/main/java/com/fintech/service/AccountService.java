package com.fintech.service;

import com.fintech.model.Account;

/**
 * A Service responsible for making actions on the account.
 * For the purpose of this task it will be implemented here, but in real life case
 * it should talk to another service via HTTP calls
 */
public interface AccountService {
    Account getAccountById(Integer getAccountId);

    boolean updateBalance(Account sourceAccount);
}
