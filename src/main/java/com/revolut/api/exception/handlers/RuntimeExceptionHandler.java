package com.revolut.api.exception.handlers;

import com.revolut.api.exception.APIErrorsRepository;
import com.revolut.api.exception.model.APIError;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
@Slf4j
public class RuntimeExceptionHandler implements ExceptionMapper<RuntimeException> {
    @Override
    public Response toResponse(RuntimeException e) {
        log.error("Generic Runtime Exception Occurred with stack trace: {}", e);
        APIError apiError = APIErrorsRepository.getErrorByCode(9999);
        return Response.status(apiError.getHttpStatus())
                .entity(apiError)
                .build();
    }
}
