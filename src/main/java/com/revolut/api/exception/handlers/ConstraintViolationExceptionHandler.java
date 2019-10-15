package com.revolut.api.exception.handlers;

import com.revolut.api.exception.APIErrorsRepository;
import com.revolut.api.exception.model.APIError;
import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.Iterator;

@Provider
@Slf4j
public class ConstraintViolationExceptionHandler implements ExceptionMapper<ConstraintViolationException> {


    @Override
    public Response toResponse(ConstraintViolationException exception) {
        APIError apiError = APIErrorsRepository.getErrorByCode(1000);
        StringBuilder messageBuilder = new StringBuilder();
        for (Iterator<ConstraintViolation<?>> iterator = exception.getConstraintViolations().iterator(); iterator.hasNext(); ) {
            ConstraintViolation<?> constraintViolation = iterator.next();
            messageBuilder.append(constraintViolation.getMessageTemplate());
            if (iterator.hasNext())
                messageBuilder.append(" ,");
        }
        apiError.setMessage(messageBuilder.toString());
        log.error(messageBuilder.toString());
        return Response.status(apiError.getHttpStatus())
                .entity(apiError)
                .build();
    }

}