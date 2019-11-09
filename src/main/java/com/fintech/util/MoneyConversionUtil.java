package com.fintech.util;

import com.fintech.exceptions.ConversionNotSupportedException;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.HashSet;
import java.util.Set;

public class MoneyConversionUtil {
    private static Set<CurrencyConversionRate> rates;

    static {
        rates = new HashSet<>();
        rates.add(CurrencyConversionRate.builder()
                .sourceCurrency(Currency.getInstance("USD"))
                .targetCurrency(Currency.getInstance("EUR"))
                .rate(new BigDecimal(0.91))
                .build());

        rates.add(CurrencyConversionRate.builder()
                .sourceCurrency(Currency.getInstance("EUR"))
                .targetCurrency(Currency.getInstance("USD"))
                .rate(new BigDecimal(1.10))
                .build());
    }

    public static BigDecimal convert(BigDecimal amount, Currency sourceCurrency, Currency targetCurrency) {
        BigDecimal result = null;
        if (sourceCurrency.equals(targetCurrency)) {
            result = amount;
        } else {
            CurrencyConversionRate currencyConversionRate = rates.stream().filter(rate ->
                    rate.getSourceCurrency().equals(sourceCurrency) && rate.getTargetCurrency().equals(targetCurrency)
            ).findFirst().orElseThrow(ConversionNotSupportedException::new);
            result = amount.multiply(currencyConversionRate.getRate());
        }
        return result;
    }
}
