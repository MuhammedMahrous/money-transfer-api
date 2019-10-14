package com.revolut.test.integration.api;

import com.revolut.model.MoneyTransfer;
import com.revolut.test.util.TestsUtil;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static io.restassured.RestAssured.given;
import static io.restassured.config.JsonConfig.jsonConfig;
import static io.restassured.path.json.config.JsonPathConfig.NumberReturnType.BIG_DECIMAL;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@QuarkusTest
public class MoneyTransferAPITests {

    @Test
    @DisplayName("Happy Money Transfer Scenario")
    public void happyScenario() throws IOException {

        MoneyTransfer moneyTransfer = TestsUtil.readMoneyTransferFromFile("/requests/happyScenario.json");
        given()
                .config(RestAssured.config().jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL)))
                .when()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body(moneyTransfer)
                .post("/moneyTransfer")
                .then()
                .statusCode(201).
                body("id", notNullValue(),
                        "sourceAccountId", equalTo(moneyTransfer.getSourceAccountId()),
                        "targetAccountId", equalTo(moneyTransfer.getTargetAccountId()),
                        "amount", equalTo(moneyTransfer.getAmount()),
                        "currency", equalTo(moneyTransfer.getCurrency().toString()));
    }

    @Test
    @DisplayName("Test Source Account Id Must Exist")
    void sourceAccountIdRequired() throws IOException {
        MoneyTransfer moneyTransfer = TestsUtil.readMoneyTransferFromFile("/requests/missingSourceAccountId.json");
        given()
                .when()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body(moneyTransfer)
                .post("/moneyTransfer")
                .then()
                .statusCode(400).
                body("code", equalTo(1000),
                        "message", equalTo("sourceAccountId can't be null"));

    }

    @Test
    @DisplayName("Test Target Account Id Must Exist")
    void targetAccountIdRequired() throws IOException {
        MoneyTransfer moneyTransfer = TestsUtil.readMoneyTransferFromFile("/requests/missingTargetAccountId.json");
        given()
                .when()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body(moneyTransfer)
                .post("/moneyTransfer")
                .then()
                .statusCode(400).
                body("code", equalTo(1000),
                        "message", equalTo("targetAccountId can't be null"));

    }

    @Test
    @DisplayName("Test Amount Must Exist")
    void amountRequired() throws IOException {
        MoneyTransfer moneyTransfer = TestsUtil.readMoneyTransferFromFile("/requests/missingAmount.json");
        given()
                .when()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body(moneyTransfer)
                .post("/moneyTransfer")
                .then()
                .statusCode(400).
                body("code", equalTo(1000),
                        "message", equalTo("amount can't be null"));

    }

    @Test
    @DisplayName("Test Currency Must Exist")
    void currencyRequired() throws IOException {
        MoneyTransfer moneyTransfer = TestsUtil.readMoneyTransferFromFile("/requests/missingCurrency.json");
        given()
                .when()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body(moneyTransfer)
                .post("/moneyTransfer")
                .then()
                .statusCode(400).
                body("code", equalTo(1000),
                        "message", equalTo("currency can't be null"));

    }

    @Test
    @DisplayName("Test Unkown Currency Not Accepted")
    void unkownCurrency() throws IOException {
        String moneyTransferRequest = TestsUtil.readStringMoneyTransferFromFile("/requests/unkownCurrency.json");
        given()
                .when()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body(moneyTransferRequest)
                .post("/moneyTransfer")
                .then()
                .statusCode(400).
                body("code", equalTo(1000),
                        "message", equalTo("Couldn't map value: [XYZ] into type: [Currency]"));

    }

    // TODO: Cover NoSuchAccountException
}
