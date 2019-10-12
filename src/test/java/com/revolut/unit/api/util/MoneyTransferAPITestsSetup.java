package com.revolut.unit.api.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revolut.model.MoneyTransfer;

import java.io.IOException;

public class MoneyTransferAPITestsSetup {

    public static MoneyTransfer readMoneyTransferRequestFromFile(String requestJsonFilePath) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(MoneyTransferAPITestsSetup.class.getResourceAsStream(requestJsonFilePath), MoneyTransfer.class);
    }
}
