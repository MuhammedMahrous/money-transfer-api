package com.revolut.test.integration.service;

import com.revolut.exceptions.NoSuchAccountException;
import com.revolut.exceptions.SameAccountException;
import com.revolut.model.Account;
import com.revolut.model.MoneyTransfer;
import com.revolut.service.AccountService;
import com.revolut.service.MoneyTransferService;
import com.revolut.test.util.TestsUtil;
import com.revolut.util.MoneyConversionUtil;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@QuarkusTest
public class MoneyTransferServiceTests {
    private MoneyTransferService moneyTransferService;
    private AccountService accountService;

    public MoneyTransferServiceTests(MoneyTransferService moneyTransferService, AccountService accountService) {
        this.moneyTransferService = moneyTransferService;
        this.accountService = accountService;
    }

    @Test
    void happyScenario() throws IOException {
        MoneyTransfer moneyTransfer = TestsUtil.readMoneyTransferFromFile("/requests/happyScenario.json");

        // get Source Account Balance
        Account beforeSourceAccount = accountService.getAccountById(moneyTransfer.getSourceAccountId());

        // get Target Account Balance
        Account beforeTargetAccount = accountService.getAccountById(moneyTransfer.getTargetAccountId());

        moneyTransferService.transferMoney(moneyTransfer);

        // Assert target account has new amount added
        Account afterSourceAccount = accountService.getAccountById(moneyTransfer.getSourceAccountId());
        // Transform amount to source account currency
        BigDecimal expectedSubtractedAmount = MoneyConversionUtil
                .convert(moneyTransfer.getAmount(), moneyTransfer.getCurrency(), afterSourceAccount.getCurrency());
        BigDecimal actualSubtractedAmount = beforeSourceAccount.getBalance().subtract(afterSourceAccount.getBalance());
        assertEquals(expectedSubtractedAmount, actualSubtractedAmount);

        // Assert target account has new amount removed
        Account afterTargetAccount = accountService.getAccountById(moneyTransfer.getTargetAccountId());
        // Transform amount to source account currency
        BigDecimal expectedAddedAmount = MoneyConversionUtil
                .convert(moneyTransfer.getAmount(), moneyTransfer.getCurrency(), afterTargetAccount.getCurrency());
        BigDecimal actualAddedAmount = afterTargetAccount.getBalance().subtract(beforeTargetAccount.getBalance());
        assertEquals(expectedAddedAmount, actualAddedAmount);
        // MoneyTransfer history updated
        //TODO: Update
    }

    @Test
    @DisplayName("Money transfer to same account not accepted")
    public void sameAccount() throws IOException {
        MoneyTransfer moneyTransfer = TestsUtil.readMoneyTransferFromFile("/requests/sameAccount.json");
        assertThrows(SameAccountException.class, () -> moneyTransferService.transferMoney(moneyTransfer));
    }
}
