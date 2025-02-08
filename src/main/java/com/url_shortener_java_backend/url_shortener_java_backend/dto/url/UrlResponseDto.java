package com.url_shortener_java_backend.url_shortener_java_backend.dto.url;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UrlResponseDto {
    private String originalUrl;
    private String shortUrl;
    private LocalDateTime creationDateTime;
    private LocalDateTime expirationDateTime;
    private String userId;
}
