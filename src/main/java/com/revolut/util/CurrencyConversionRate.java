package com.revolut.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Currency;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CurrencyConversionRate {
    private BigDecimal rate;
    private Currency sourceCurrency;
    private Currency targetCurrency;
}
