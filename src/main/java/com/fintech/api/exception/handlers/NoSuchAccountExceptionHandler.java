package com.fintech.api.exception.handlers;

import com.fintech.api.exception.APIErrorsRepository;
import com.fintech.api.exception.model.APIError;
import com.fintech.exceptions.NoSuchAccountException;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
@Slf4j
public class NoSuchAccountExceptionHandler implements ExceptionMapper<NoSuchAccountException> {
    @Override
    public Response toResponse(NoSuchAccountException exception) {
        log.error(exception.getMessage());
        APIError apiError = APIErrorsRepository.getErrorByCode(1001);
        apiError.setMessage(exception.getMessage());
        return Response.status(apiError.getHttpStatus())
                .entity(apiError)
                .build();
    }
}
