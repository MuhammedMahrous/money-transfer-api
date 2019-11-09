package com.fintech.api.exception.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.ws.rs.core.Response;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class APIError {
    private Integer code;
    private String message;
    @JsonIgnore
    private Response.Status httpStatus;
}

