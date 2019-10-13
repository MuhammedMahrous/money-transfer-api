package com.revolut.unit.api.setup;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revolut.model.MoneyTransfer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MoneyTransferAPITestsSetup {

    public static MoneyTransfer readMoneyTransferRequestFromFile(String requestJsonFilePath) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(MoneyTransferAPITestsSetup.class.getResourceAsStream(requestJsonFilePath), MoneyTransfer.class);
    }

    public static String readStringMoneyTransferRequestFromFile(String requestJsonFilePath) throws IOException {
        InputStream inputStream = MoneyTransferAPITestsSetup.class.getResourceAsStream(requestJsonFilePath);

        StringBuilder stringBuilder = new StringBuilder();
        String line = null;

        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
        }

        return stringBuilder.toString();

    }
}
