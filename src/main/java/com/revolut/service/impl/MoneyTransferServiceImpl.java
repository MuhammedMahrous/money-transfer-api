package com.revolut.service.impl;

import com.revolut.exceptions.SameAccountException;
import com.revolut.model.Account;
import com.revolut.model.MoneyTransfer;
import com.revolut.service.AccountService;
import com.revolut.service.MoneyTransferService;
import com.revolut.util.MoneyConversionUtil;

import javax.enterprise.context.ApplicationScoped;
import java.math.BigDecimal;
import java.util.Random;

@ApplicationScoped
public class MoneyTransferServiceImpl implements MoneyTransferService {

    private AccountService accountService;

    public MoneyTransferServiceImpl(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public MoneyTransfer transferMoney(MoneyTransfer moneyTransfer) {
        Random random = new Random();
        moneyTransfer.setId(random.nextInt() * 100000);

        if (moneyTransfer.getSourceAccountId().equals(moneyTransfer.getTargetAccountId()))
            throw new SameAccountException();

        Account sourceAccount = accountService.getAccountById(moneyTransfer.getSourceAccountId());
        Account targetAccount = accountService.getAccountById(moneyTransfer.getTargetAccountId());

        BigDecimal amountInSourceCurrency =
                MoneyConversionUtil.convert(moneyTransfer.getAmount(),
                        moneyTransfer.getCurrency(), sourceAccount.getCurrency());

        BigDecimal amountInTargetCurrency =
                MoneyConversionUtil.convert(moneyTransfer.getAmount(),
                        moneyTransfer.getCurrency(), targetAccount.getCurrency());

        sourceAccount.setBalance(sourceAccount.getBalance().subtract(amountInSourceCurrency));

        targetAccount.setBalance(targetAccount.getBalance().add(amountInTargetCurrency));

        accountService.updateBalance(sourceAccount);
        accountService.updateBalance(targetAccount);

        return moneyTransfer;
    }
}
