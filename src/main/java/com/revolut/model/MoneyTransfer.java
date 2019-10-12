package com.revolut.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Currency;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MoneyTransfer {
    private Integer id;

    @NotNull(message = "sourceAccountId can't be null")
    private Integer sourceAccountId;

    @NotNull(message = "targetAccountId can't be null")
    private Integer targetAccountId;

    @NotNull(message = "amount can't be null")
    private BigDecimal amount;

    @NotNull(message = "currency can't be null")
    private Currency currency;
}

