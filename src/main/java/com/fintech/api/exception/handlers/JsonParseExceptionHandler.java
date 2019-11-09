package com.fintech.api.exception.handlers;

import com.fasterxml.jackson.core.JsonParseException;
import com.fintech.api.exception.APIErrorsRepository;
import com.fintech.api.exception.model.APIError;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
@Slf4j
public class JsonParseExceptionHandler implements ExceptionMapper<JsonParseException> {
    @Override
    public Response toResponse(JsonParseException e) {
        log.error("Invalid JSON was received, stack trace: {}", e);
        APIError apiError = APIErrorsRepository.getErrorByCode(1001);
        apiError.setMessage("Invalid JSON");
        return Response.status(apiError.getHttpStatus())
                .entity(apiError)
                .build();
    }
}
