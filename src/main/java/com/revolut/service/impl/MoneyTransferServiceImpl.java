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
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.TransactionManager;
import java.math.BigDecimal;
import java.sql.SQLException;

@ApplicationScoped
@Slf4j
public class MoneyTransferServiceImpl implements MoneyTransferService {

    private AccountService accountService;
    private MoneyTransferRepository moneyTransferRepository;
    private TransactionManager transactionManager;

    public MoneyTransferServiceImpl(
            AccountService accountService,
            MoneyTransferRepository moneyTransferRepository,
            TransactionManager transactionManager) {
        this.accountService = accountService;
        this.moneyTransferRepository = moneyTransferRepository;
        this.transactionManager = transactionManager;
    }

    @Override
    @Synchronized
    public MoneyTransfer transferMoney(MoneyTransfer moneyTransfer) throws Exception {
        transactionManager.begin();
        try {
            if (moneyTransfer.getSourceAccountId().equals(moneyTransfer.getTargetAccountId())) {
                throw new SameAccountException();
            }

            Account sourceAccount = accountService.getAccountById(moneyTransfer.getSourceAccountId());
            Account targetAccount = accountService.getAccountById(moneyTransfer.getTargetAccountId());
            log.debug("sourceAccount before money transfer: {}", sourceAccount);
            log.debug("targetAccount before money transfer: {}", targetAccount);

            BigDecimal amountInSourceCurrency =
                    MoneyConversionUtil.convert(moneyTransfer.getAmount(),
                            moneyTransfer.getCurrency(), sourceAccount.getCurrency());
            log.debug("amount to be taken from source account: {}", amountInSourceCurrency);

            validateAmount(sourceAccount, amountInSourceCurrency);

            BigDecimal amountInTargetCurrency =
                    MoneyConversionUtil.convert(moneyTransfer.getAmount(),
                            moneyTransfer.getCurrency(), targetAccount.getCurrency());
            log.debug("amount to be taken added to target account: {}", amountInTargetCurrency);

            sourceAccount.setBalance(sourceAccount.getBalance().subtract(amountInSourceCurrency));
            targetAccount.setBalance(targetAccount.getBalance().add(amountInTargetCurrency));

            accountService.updateBalance(sourceAccount);
            accountService.updateBalance(targetAccount);

            log.debug("sourceAccount after money transfer: {}", sourceAccount);
            log.debug("targetAccount after money transfer: {}", targetAccount);

            create(moneyTransfer);
            transactionManager.commit();

        } catch (Exception e) {
            log.error("An error occurred while transferring money, will rollback");
            log.error("Stack trace: {}", e);
            transactionManager.rollback();
            throw e;
        }
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
