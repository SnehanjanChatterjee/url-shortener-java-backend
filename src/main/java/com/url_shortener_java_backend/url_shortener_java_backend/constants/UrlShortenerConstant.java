package com.url_shortener_java_backend.url_shortener_java_backend.constants;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class UrlShortenerConstant {
    @Value("${app.backend.base.url}")
    private String shortenedUrlBase;

    @Value("${app.frontend.local.base.url}")
    private String frontendLocalUrl;

    @Value("${app.frontend.cloud.base.url}")
    private String frontendCloudUrl;
}
