package com.url_shortener_java_backend.url_shortener_java_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UrlRequestDto {
    private String url;
    private String expirationDateTime;
}
