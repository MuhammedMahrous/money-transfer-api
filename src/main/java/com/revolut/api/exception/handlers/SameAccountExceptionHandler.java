package com.revolut.api.exception.handlers;

import com.revolut.api.exception.APIErrorsRepository;
import com.revolut.api.exception.model.APIError;
import com.revolut.exceptions.SameAccountException;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
@Slf4j
public class SameAccountExceptionHandler implements ExceptionMapper<SameAccountException> {
    @Override
    public Response toResponse(SameAccountException exception) {
        log.error(exception.getMessage());
        APIError apiError = APIErrorsRepository.getErrorByCode(1000);
        apiError.setMessage(exception.getMessage());
        return Response.status(apiError.getHttpStatus())
                .entity(apiError)
                .build();
    }
}
