package com.revolut.unit.api.mocks;

import com.revolut.model.MoneyTransfer;
import com.revolut.service.MoneyTransferService;
import io.quarkus.test.Mock;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import java.util.Random;

@Mock
@ApplicationScoped
@Slf4j
public class MoneyTransferMockService implements MoneyTransferService {
    private Random random;

    public MoneyTransferMockService() {
        this.random = new Random();
    }

    @Override
    public MoneyTransfer transferMoney(MoneyTransfer moneyTransfer) {
        moneyTransfer.setId(random.nextInt() * 20000);
        return moneyTransfer;
    }
}
