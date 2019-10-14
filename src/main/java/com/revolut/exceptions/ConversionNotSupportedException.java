package com.revolut.exceptions;

public class ConversionNotSupportedException extends RuntimeException {
    public ConversionNotSupportedException() {
    }

    public ConversionNotSupportedException(String sourceCurrency, String targetCurrency) {
        super("Can't convert from [" + sourceCurrency + "] to [" + targetCurrency + "]");
    }

    public ConversionNotSupportedException(String message) {
        super(message);
    }
}
