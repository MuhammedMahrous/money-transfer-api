package com.fintech.test.integration.service;

import com.fintech.exceptions.NegativeAmountException;
import com.fintech.exceptions.NoEnoughBalanceException;
import com.fintech.exceptions.NoSuchMoneyTransfer;
import com.fintech.exceptions.SameAccountException;
import com.fintech.model.Account;
import com.fintech.model.MoneyTransfer;
import com.fintech.service.AccountService;
import com.fintech.service.MoneyTransferService;
import com.fintech.test.util.TestsUtil;
import com.fintech.util.MoneyConversionUtil;
import io.quarkus.test.junit.QuarkusTest;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@Slf4j
public class MoneyTransferServiceTests {
    private MoneyTransferService moneyTransferService;
    private AccountService accountService;
    private DataSource dataSource;
    private Flyway flyway;

    public MoneyTransferServiceTests(
            MoneyTransferService moneyTransferService
            , AccountService accountService,
            DataSource dataSource,
            Flyway flyway) {
        this.moneyTransferService = moneyTransferService;
        this.accountService = accountService;
        this.dataSource = dataSource;
        this.flyway = flyway;
    }

    @BeforeEach
    void setUp() {
        flyway.clean();
        flyway.migrate();
    }

    @Test
    @DisplayName("Happy Scenario")
    public void happyScenario() throws Exception {
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
        assertThat(expectedSubtractedAmount, Matchers.comparesEqualTo(actualSubtractedAmount));

        // Assert target account has new amount removed
        Account afterTargetAccount = accountService.getAccountById(requestMoneyTransfer.getTargetAccountId());
        // Transform amount to source account currency
        BigDecimal expectedAddedAmount = MoneyConversionUtil
                .convert(requestMoneyTransfer.getAmount(), requestMoneyTransfer.getCurrency(), afterTargetAccount.getCurrency());
        BigDecimal actualAddedAmount = afterTargetAccount.getBalance().subtract(beforeTargetAccount.getBalance());
        assertThat(expectedAddedAmount, Matchers.comparesEqualTo(actualAddedAmount));

        // MoneyTransfer history updated
        MoneyTransfer actualMoneyTransfer = moneyTransferService.getById(requestMoneyTransfer.getId());

        validateMoneyTransfer(requestMoneyTransfer, actualMoneyTransfer);
    }

    @Test
    @DisplayName("Transaction Support")
    void transactionSupport() throws Exception {
        MoneyTransfer requestMoneyTransfer = TestsUtil.readMoneyTransferFromFile("/requests/happyScenario.json");

        Account beforeSourceAccount = accountService.getAccountById(requestMoneyTransfer.getSourceAccountId());
        Account beforeTargetAccount = accountService.getAccountById(requestMoneyTransfer.getTargetAccountId());

        // Drop money-transfer table to simulate an exception
        dropMoneyTransferTable();
        try {
            moneyTransferService.transferMoney(requestMoneyTransfer);
        } catch (Exception e) {
            log.info("Exception is thrown as anticipated");
        }
        // Assert that both source and target accounts balances didn't change due to an exception happening
        Account afterSourceAccount = accountService.getAccountById(requestMoneyTransfer.getSourceAccountId());
        assertEquals(beforeSourceAccount.getBalance(), afterSourceAccount.getBalance());

        Account afterTargetAccount = accountService.getAccountById(requestMoneyTransfer.getTargetAccountId());
        assertEquals(beforeTargetAccount.getBalance(), afterTargetAccount.getBalance());

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

    private void dropMoneyTransferTable() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            connection.createStatement().execute("DROP TABLE money_transfer");
        }
    }
}
