package com.revolut.test.integration.service;

import com.revolut.model.Account;
import com.revolut.model.MoneyTransfer;
import com.revolut.service.AccountService;
import com.revolut.service.MoneyTransferService;
import com.revolut.test.util.TestsUtil;
import com.revolut.util.MoneyConversionUtil;
import io.quarkus.test.junit.QuarkusTest;
import org.flywaydb.core.Flyway;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;

@QuarkusTest
public class MoneyTransferServiceConcurrencyTests {
    private static final Integer NUM_THREADS = 10;
    private MoneyTransferServiceTests moneyTransferServiceTests;
    private AccountService accountService;

    public MoneyTransferServiceConcurrencyTests(
            MoneyTransferService moneyTransferService,
            AccountService accountService,
            DataSource dataSource,
            Flyway flyway) {
        this.accountService = accountService;
        moneyTransferServiceTests =
                new MoneyTransferServiceTests(moneyTransferService, accountService, dataSource, flyway);
    }

    @Test
    @DisplayName("Testing Concurrent Money Transfer For The Same Request")
    public void basicConcurrency() throws InterruptedException, IOException {
        MoneyTransfer requestMoneyTransfer = TestsUtil.readMoneyTransferFromFile("/requests/happyScenario.json");
        Account beforeSourceAccount = accountService.getAccountById(requestMoneyTransfer.getSourceAccountId());
        Account beforeTargetAccount = accountService.getAccountById(requestMoneyTransfer.getTargetAccountId());

        ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);
        for (int i = 0; i < NUM_THREADS; i++) {
            executor.submit(() -> {
                try {
                    moneyTransferServiceTests.happyScenario();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        Account afterSourceAccount = accountService.getAccountById(requestMoneyTransfer.getSourceAccountId());
        BigDecimal expectedSubtractedAmount = MoneyConversionUtil
                .convert(requestMoneyTransfer.getAmount().multiply(BigDecimal.TEN),
                        requestMoneyTransfer.getCurrency(), afterSourceAccount.getCurrency());
        BigDecimal actualSubtractedAmount = beforeSourceAccount.getBalance().subtract(afterSourceAccount.getBalance());
        assertThat(expectedSubtractedAmount, Matchers.comparesEqualTo(actualSubtractedAmount));

        Account afterTargetAccount = accountService.getAccountById(requestMoneyTransfer.getTargetAccountId());
        BigDecimal expectedAddedAmount = MoneyConversionUtil
                .convert(requestMoneyTransfer.getAmount().multiply(BigDecimal.TEN),
                        requestMoneyTransfer.getCurrency(), afterTargetAccount.getCurrency());
        BigDecimal actualAddedAmount = afterTargetAccount.getBalance().subtract(beforeTargetAccount.getBalance());
        assertThat(expectedAddedAmount, Matchers.comparesEqualTo(actualAddedAmount));


    }

}
