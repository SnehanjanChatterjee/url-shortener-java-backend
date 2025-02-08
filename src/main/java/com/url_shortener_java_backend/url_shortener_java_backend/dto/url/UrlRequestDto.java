package com.url_shortener_java_backend.url_shortener_java_backend.dto.url;

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
    private String userId;
}
