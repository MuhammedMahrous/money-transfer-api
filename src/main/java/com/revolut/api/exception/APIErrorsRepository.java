package com.revolut.api.exception;

import com.revolut.api.exception.model.APIError;

import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class APIErrorsRepository {
    private static Map<Integer, APIError> errorMap;

    static {
        errorMap = new HashMap<>();
        errorMap.put(1000,
                APIError
                        .builder()
                        .code(1000)
                        .httpStatus(Response.Status.BAD_REQUEST)
                        .message("Invalid Input")
                        .build());

        errorMap.put(1001,
                APIError
                        .builder()
                        .code(1001)
                        .httpStatus(Response.Status.BAD_REQUEST)
                        .message("No Such Account")
                        .build());
        errorMap.put(1002,
                APIError
                        .builder()
                        .code(1002)
                        .httpStatus(Response.Status.BAD_REQUEST)
                        .message("No Enough Balance")
                        .build());
        errorMap.put(1003,
                APIError
                        .builder()
                        .code(1003)
                        .httpStatus(Response.Status.BAD_REQUEST)
                        .message("Negative Amount")
                        .build());

        errorMap.put(9999,
                APIError
                        .builder()
                        .code(9999)
                        .httpStatus(Response.Status.INTERNAL_SERVER_ERROR)
                        .message("Generic Error Occurred")
                        .build());

    }

    public static APIError getErrorByCode(Integer code) {
        APIError error = errorMap.get(code);
        return error == null ? errorMap.get(1001) : error;
    }

    public static void main(String[] args) {
        System.out.println(BigDecimal.ZERO);
    }
}
