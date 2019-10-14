package com.revolut.test.integration.service;

import com.revolut.exceptions.NegativeAmountException;
import com.revolut.exceptions.NoEnoughBalanceException;
import com.revolut.exceptions.NoSuchMoneyTransfer;
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
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class MoneyTransferServiceTests {
    private MoneyTransferService moneyTransferService;
    private AccountService accountService;

    public MoneyTransferServiceTests(MoneyTransferService moneyTransferService, AccountService accountService) {
        this.moneyTransferService = moneyTransferService;
        this.accountService = accountService;
    }

    @Test
    @DisplayName("Happy Scenario")
    void happyScenario() throws IOException, SQLException {
        MoneyTransfer requestMoneyTransfer = TestsUtil.readMoneyTransferFromFile("/requests/happyScenario.json");

        // get Source Account Balance
        Account beforeSourceAccount = accountService.getAccountById(requestMoneyTransfer.getSourceAccountId());

        // get Target Account Balance
        Account beforeTargetAccount = accountService.getAccountById(requestMoneyTransfer.getTargetAccountId());

        moneyTransferService.transferMoney(requestMoneyTransfer);

        // Assert target account has new amount added
        Account afterSourceAccount = accountService.getAccountById(requestMoneyTransfer.getSourceAccountId());
        // Transform amount to source account currency
        BigDecimal expectedSubtractedAmount = MoneyConversionUtil
                .convert(requestMoneyTransfer.getAmount(), requestMoneyTransfer.getCurrency(), afterSourceAccount.getCurrency());
        BigDecimal actualSubtractedAmount = beforeSourceAccount.getBalance().subtract(afterSourceAccount.getBalance());
        assertEquals(expectedSubtractedAmount, actualSubtractedAmount);

        // Assert target account has new amount removed
        Account afterTargetAccount = accountService.getAccountById(requestMoneyTransfer.getTargetAccountId());
        // Transform amount to source account currency
        BigDecimal expectedAddedAmount = MoneyConversionUtil
                .convert(requestMoneyTransfer.getAmount(), requestMoneyTransfer.getCurrency(), afterTargetAccount.getCurrency());
        BigDecimal actualAddedAmount = afterTargetAccount.getBalance().subtract(beforeTargetAccount.getBalance());
        assertEquals(expectedAddedAmount, actualAddedAmount);

        // MoneyTransfer history updated
        MoneyTransfer actualMoneyTransfer = moneyTransferService.getById(requestMoneyTransfer.getId());

        validateMoneyTransfer(requestMoneyTransfer, actualMoneyTransfer);
    }

    @Test
    @DisplayName("No Enough Balance")
    public void notEnoughBalance() throws IOException {
        MoneyTransfer moneyTransfer = TestsUtil.readMoneyTransferFromFile("/requests/noEnoughBalance.json");
        assertThrows(NoEnoughBalanceException.class, () -> moneyTransferService.transferMoney(moneyTransfer));
    }

    @Test
    @DisplayName("Money transfer to same account not accepted")
    public void sameAccount() throws IOException {
        MoneyTransfer moneyTransfer = TestsUtil.readMoneyTransferFromFile("/requests/sameAccount.json");
        assertThrows(SameAccountException.class, () -> moneyTransferService.transferMoney(moneyTransfer));
    }

    @Test
    @DisplayName("Negative Amount")
    public void negativeAmount() throws IOException {
        MoneyTransfer moneyTransfer = TestsUtil.readMoneyTransferFromFile("/requests/negativeAmount.json");
        assertThrows(NegativeAmountException.class, () -> moneyTransferService.transferMoney(moneyTransfer));
    }

    @Test
    @DisplayName("Getting Already existing MoneyTransfer Transaction")
    public void getMoneyTransferById() throws IOException, SQLException {
        Integer moneyTransferId = TestsUtil.readIntFromFile("/service-inputs/getMoneyTransferId.json");
        MoneyTransfer expectedMoneyTransfer = TestsUtil
                .readMoneyTransferFromFile("/service-inputs/getMoneyTransferHappyScenario.json");

        MoneyTransfer actualMoneyTransfer = moneyTransferService.getById(moneyTransferId);
        assertNotNull(actualMoneyTransfer);
        assertEquals(expectedMoneyTransfer, actualMoneyTransfer);
    }

    @Test
    @DisplayName("No MoneyTransfer with given id")
    public void noSuchMoneyTransfer() throws IOException, SQLException {
        Integer moneyTransferId = TestsUtil.readIntFromFile("/service-inputs/invalidMoneyTransferId.json");
        assertThrows(NoSuchMoneyTransfer.class, () -> moneyTransferService.getById(moneyTransferId));

    }

    @Test
    @DisplayName("Create MoneyTransfer")
    public void createMoneyTransfer() throws IOException, SQLException {
        MoneyTransfer expectedMoneyTransfer = TestsUtil
                .readMoneyTransferFromFile("/service-inputs/createMoneyTransferHappyScenario.json");
        MoneyTransfer actualMoneyTransfer = moneyTransferService.create(expectedMoneyTransfer);
        validateMoneyTransfer(expectedMoneyTransfer, actualMoneyTransfer);
    }

    private void validateMoneyTransfer(MoneyTransfer expected, MoneyTransfer actual) {
        assertNotNull(actual);
        assertNotNull(actual.getId());
        assertEquals(expected.getAmount(), actual.getAmount());
        assertEquals(expected.getSourceAccountId(), actual.getSourceAccountId());
        assertEquals(expected.getTargetAccountId(), actual.getTargetAccountId());
        assertEquals(expected.getCurrency(), actual.getCurrency());
    }
}
