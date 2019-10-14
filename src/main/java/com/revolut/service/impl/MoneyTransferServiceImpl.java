package com.revolut.service.impl;

import com.revolut.exceptions.NegativeAmountException;
import com.revolut.exceptions.NoEnoughBalanceException;
import com.revolut.exceptions.SameAccountException;
import com.revolut.model.Account;
import com.revolut.model.MoneyTransfer;
import com.revolut.repository.MoneyTransferRepository;
import com.revolut.service.AccountService;
import com.revolut.service.MoneyTransferService;
import com.revolut.util.MoneyConversionUtil;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import java.math.BigDecimal;
import java.sql.SQLException;

@ApplicationScoped
@Slf4j
public class MoneyTransferServiceImpl implements MoneyTransferService {

    private AccountService accountService;
    private MoneyTransferRepository moneyTransferRepository;

    public MoneyTransferServiceImpl(AccountService accountService, MoneyTransferRepository moneyTransferRepository) {
        this.accountService = accountService;
        this.moneyTransferRepository = moneyTransferRepository;
    }

    @Override
    public MoneyTransfer transferMoney(MoneyTransfer moneyTransfer) throws SQLException {
        if (moneyTransfer.getSourceAccountId().equals(moneyTransfer.getTargetAccountId()))
            throw new SameAccountException();

        Account sourceAccount = accountService.getAccountById(moneyTransfer.getSourceAccountId());
        Account targetAccount = accountService.getAccountById(moneyTransfer.getTargetAccountId());
        log.info("sourceAccount before money transfer: {}", sourceAccount);
        log.info("targetAccount before money transfer: {}", targetAccount);

        BigDecimal amountInSourceCurrency =
                MoneyConversionUtil.convert(moneyTransfer.getAmount(),
                        moneyTransfer.getCurrency(), sourceAccount.getCurrency());

        validateAmount(sourceAccount, amountInSourceCurrency);

        BigDecimal amountInTargetCurrency =
                MoneyConversionUtil.convert(moneyTransfer.getAmount(),
                        moneyTransfer.getCurrency(), targetAccount.getCurrency());

        sourceAccount.setBalance(sourceAccount.getBalance().subtract(amountInSourceCurrency));

        targetAccount.setBalance(targetAccount.getBalance().add(amountInTargetCurrency));

        accountService.updateBalance(sourceAccount);
        accountService.updateBalance(targetAccount);

        log.info("sourceAccount after money transfer: {}", sourceAccount);
        log.info("targetAccount after money transfer: {}", targetAccount);


        create(moneyTransfer);
        return moneyTransfer;
    }

    private void validateAmount(Account sourceAccount, BigDecimal amountInSourceCurrency) {
        if (amountInSourceCurrency.compareTo(BigDecimal.ZERO) < 0)
            throw new NegativeAmountException();
        if (sourceAccount.getBalance().compareTo(amountInSourceCurrency) < 0)
            throw new NoEnoughBalanceException(sourceAccount.getId());
    }

    @Override
    public MoneyTransfer getById(Integer id) throws SQLException {
        return moneyTransferRepository.getById(id);
    }

    @Override
    public MoneyTransfer create(MoneyTransfer moneyTransfer) throws SQLException {
        return moneyTransferRepository.create(moneyTransfer);
    }
}
