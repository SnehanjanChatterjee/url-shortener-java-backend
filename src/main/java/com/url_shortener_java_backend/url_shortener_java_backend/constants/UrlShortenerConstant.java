package com.url_shortener_java_backend.url_shortener_java_backend.constants;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class UrlShortenerConstant {
    @Value("${app.base.url}")
    private String shortenedUrlBase;
}
