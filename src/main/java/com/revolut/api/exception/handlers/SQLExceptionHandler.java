package com.revolut.api.exception.handlers;

import com.revolut.api.exception.APIErrorsRepository;
import com.revolut.api.exception.model.APIError;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.sql.SQLException;

@Provider
@Slf4j
public class SQLExceptionHandler implements ExceptionMapper<SQLException> {
    @Override
    public Response toResponse(SQLException exception) {
        log.error(exception.getMessage());
        APIError apiError = APIErrorsRepository.getErrorByCode(9999);
        return Response.status(apiError.getHttpStatus())
                .entity(apiError)
                .build();
    }
}
