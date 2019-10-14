package com.revolut.api.exception.handlers;

import com.revolut.api.exception.APIErrorsRepository;
import com.revolut.api.exception.model.APIError;
import com.revolut.exceptions.NoSuchAccountException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class NoSuchAccountExceptionHandler implements ExceptionMapper<NoSuchAccountException> {
    @Override
    public Response toResponse(NoSuchAccountException exception) {
        APIError apiError = APIErrorsRepository.getErrorByCode(1001);
        apiError.setMessage(exception.getMessage());
        return Response.status(apiError.getHttpStatus())
                .entity(apiError)
                .build();
    }
}
