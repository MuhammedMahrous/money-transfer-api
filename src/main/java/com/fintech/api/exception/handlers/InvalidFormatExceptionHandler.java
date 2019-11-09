package com.fintech.api.exception.handlers;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fintech.api.exception.APIErrorsRepository;
import com.fintech.api.exception.model.APIError;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
@Slf4j
public class InvalidFormatExceptionHandler implements ExceptionMapper<InvalidFormatException> {


    @Override
    public Response toResponse(InvalidFormatException exception) {
        APIError apiError = APIErrorsRepository.getErrorByCode(1000);
        StringBuilder messageBuilder = new StringBuilder();
        String failedParsingTypeName = exception.getTargetType().getSimpleName();
        String failedParsingValue = exception.getValue().toString();
        messageBuilder.append("Couldn't map value: [");
        messageBuilder.append(failedParsingValue);
        messageBuilder.append("] into type: [");
        messageBuilder.append(failedParsingTypeName);
        messageBuilder.append("]");
        apiError.setMessage(messageBuilder.toString());
        log.error(messageBuilder.toString());
        return Response.status(apiError.getHttpStatus())
                .entity(apiError)
                .build();
    }

}