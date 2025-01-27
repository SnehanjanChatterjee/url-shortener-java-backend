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

    @Value("${app.frontend.cloud.base.url.2}")
    private String frontendCloudUrl2;

    @Value("${app.frontend.cloud.base.url.3}")
    private String frontendCloudUrl3;

    @Value("${app.frontend.cloud.base.url.4}")
    private String frontendCloudUrl4;
}
