package com.revolut.test.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revolut.model.Account;
import com.revolut.model.MoneyTransfer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Scanner;

public class TestsUtil {

    public static MoneyTransfer readMoneyTransferFromFile(String jsonFilePath) throws IOException {
        return (MoneyTransfer) readFromJsonFile(jsonFilePath, MoneyTransfer.class);
    }

    public static Account readAccountFromFile(String jsonFilePath) throws IOException {
        return (Account) readFromJsonFile(jsonFilePath, Account.class);
    }

    private static Object readFromJsonFile(String jsonFilePath, Class clazz) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(TestsUtil.class.getResourceAsStream(jsonFilePath), clazz);
    }


    public static String readStringMoneyTransferFromFile(String requestJsonFilePath) throws IOException {
        InputStream inputStream = TestsUtil.class.getResourceAsStream(requestJsonFilePath);

        StringBuilder stringBuilder = new StringBuilder();
        String line = null;

        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
        }

        return stringBuilder.toString();

    }

    public static Integer readAccountIdFromFile(String accountIdFilePath) {
        InputStream inputStream = TestsUtil.class.getResourceAsStream(accountIdFilePath);
        Scanner scanner = new Scanner(inputStream);
        return scanner.nextInt();
    }
}
