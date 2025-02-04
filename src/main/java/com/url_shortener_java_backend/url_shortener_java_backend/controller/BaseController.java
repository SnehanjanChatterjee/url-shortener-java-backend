package com.url_shortener_java_backend.url_shortener_java_backend.controller;

import com.url_shortener_java_backend.url_shortener_java_backend.dto.ErrorResponseData;
import com.url_shortener_java_backend.url_shortener_java_backend.dto.RestResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class BaseController {
    public <T> ResponseEntity<RestResponse<T>> getErrorResponseEntity(
            @Nullable final Exception e,
            @Nonnull final HttpStatus httpStatus,
            final RestResponse<T> restResponse,
            final String errorMessage) {

        final ErrorResponseData errorResponseData = ErrorResponseData.builder()
                .errorMessage(errorMessage)
                .errorCode(httpStatus.toString())
                .details(e != null ? e.getMessage() : null)
                .build();

        restResponse.setStatus("failure");
        restResponse.setErrorData(errorResponseData);

        return new ResponseEntity<>(restResponse, httpStatus);
    }
}
