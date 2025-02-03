package com.url_shortener_java_backend.url_shortener_java_backend.controller;

import com.url_shortener_java_backend.url_shortener_java_backend.dto.ErrorResponseData;
import com.url_shortener_java_backend.url_shortener_java_backend.dto.RestResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public abstract class BaseController {
    public <T> ResponseEntity<RestResponse<T>> getErrorResponseEntity(
            final Exception e,
            final RestResponse<T> restResponse,
            final String errorMessage) {

        final ErrorResponseData errorResponseData = ErrorResponseData.builder()
                .errorMessage(errorMessage)
                .errorCode(HttpStatus.INTERNAL_SERVER_ERROR.toString())
                .details(e.getMessage())
                .build();

        restResponse.setStatus("failure");
        restResponse.setErrorData(errorResponseData);

        return new ResponseEntity<>(restResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
