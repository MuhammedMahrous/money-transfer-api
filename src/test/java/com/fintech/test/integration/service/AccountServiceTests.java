package com.fintech.test.integration.service;

import com.fintech.exceptions.NoSuchAccountException;
import com.fintech.model.Account;
import com.fintech.service.AccountService;
import com.fintech.test.util.TestsUtil;
import io.quarkus.test.junit.QuarkusTest;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class AccountServiceTests {

    private AccountService accountService;
    private Flyway flyway;

    public AccountServiceTests(AccountService accountService, Flyway flyway) {
        this.accountService = accountService;
        this.flyway = flyway;
    }

    @BeforeEach
    void setUp() {
        flyway.clean();
        flyway.migrate();
    }

    @Test
    @DisplayName("Test Get Account By Id Happy Scenario")
    void getAccountByIdHappyScenario() throws IOException {
        Integer getAccountId = TestsUtil.readIntFromFile("/service-inputs/getAccountId.json");
        Account expected = TestsUtil.readAccountFromFile("/service-inputs/getAccountHappyScenario.json");
        Account actual = accountService.getAccountById(getAccountId);
        assertNotNull(actual);
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getBalance(), actual.getBalance());
        assertEquals(expected.getCurrency(), actual.getCurrency());
    }

    @Test
    @DisplayName("Test NoSuchAccountException")
    void noSuchAccount() throws IOException {
        Integer getAccountId = 123697215;
        assertThrows(NoSuchAccountException.class, () -> {
            accountService.getAccountById(getAccountId);
        });
    }

    @Test
    @DisplayName("Test Update Balance Happy Scenario")
    void updateBalance() throws IOException {
        Integer getAccountId = TestsUtil.readIntFromFile("/service-inputs/getAccountId.json");
        Account account = accountService.getAccountById(getAccountId);
        account.setBalance(new BigDecimal(200));
        boolean updated = accountService.updateBalance(account);
        assertTrue(updated);
        BigDecimal actual = accountService.getAccountById(getAccountId).getBalance();
        BigDecimal expected = new BigDecimal(200);
        assertEquals(expected, actual);
    }
}
