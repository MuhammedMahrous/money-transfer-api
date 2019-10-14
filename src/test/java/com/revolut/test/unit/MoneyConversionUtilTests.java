package com.revolut.test.unit;

import com.revolut.exceptions.ConversionNotSupportedException;
import com.revolut.util.MoneyConversionUtil;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MoneyConversionUtilTests {
    @Test
    void testUSDotEURconversion() {
        BigDecimal actual = MoneyConversionUtil.convert(new BigDecimal(10), Currency.getInstance("USD"), Currency.getInstance("EUR"));
        BigDecimal expected = new BigDecimal(10).multiply(new BigDecimal(0.91));
        assertEquals(expected, actual);
    }

    @Test
    void testEURotUSDconversion() {
        BigDecimal actual = MoneyConversionUtil.convert(new BigDecimal(10), Currency.getInstance("EUR"), Currency.getInstance("USD"));
        BigDecimal expected = new BigDecimal(10).multiply(new BigDecimal(1.10));
        assertEquals(expected, actual);
    }

    @Test
    void testEURotEURconversion() {
        BigDecimal actual = MoneyConversionUtil.convert(new BigDecimal(10), Currency.getInstance("EUR"), Currency.getInstance("EUR"));
        BigDecimal expected = new BigDecimal(10);
        assertEquals(expected, actual);
    }

    @Test
    void testUnknownCurrency() {
        assertThrows(IllegalArgumentException.class,() -> {
            MoneyConversionUtil.convert(new BigDecimal(10), Currency.getInstance("XYZ"), Currency.getInstance("GHJ"));
        });
    }

    @Test
    void testConversionNotSupportedException() {
        assertThrows(ConversionNotSupportedException.class,() -> {
            MoneyConversionUtil.convert(new BigDecimal(10), Currency.getInstance("USD"), Currency.getInstance("EGP"));
        });
    }
}
