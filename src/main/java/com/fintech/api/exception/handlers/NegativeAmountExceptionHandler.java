package com.fintech.api.exception.handlers;

import com.fintech.api.exception.APIErrorsRepository;
import com.fintech.api.exception.model.APIError;
import com.fintech.exceptions.NegativeAmountException;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
@Slf4j
public class NegativeAmountExceptionHandler implements ExceptionMapper<NegativeAmountException> {
    @Override
    public Response toResponse(NegativeAmountException exception) {
        log.error(exception.getMessage());
        APIError apiError = APIErrorsRepository.getErrorByCode(1003);
        apiError.setMessage(exception.getMessage());
        return Response.status(apiError.getHttpStatus())
                .entity(apiError)
                .build();
    }
}
