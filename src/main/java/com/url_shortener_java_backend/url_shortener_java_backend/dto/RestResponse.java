package com.url_shortener_java_backend.url_shortener_java_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RestResponse<T> {
    protected String status = "success";
    protected T result;
    protected ErrorResponseData errorData = null;
}
