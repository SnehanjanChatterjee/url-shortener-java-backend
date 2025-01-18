package com.url_shortener_java_backend.url_shortener_java_backend.service;

import com.url_shortener_java_backend.url_shortener_java_backend.dto.UrlRequestDto;
import com.url_shortener_java_backend.url_shortener_java_backend.dto.UrlResponseDto;
import com.url_shortener_java_backend.url_shortener_java_backend.model.Url;
import lombok.NonNull;
import org.springframework.stereotype.Service;

@Service
public interface UrlShortenerService {
    public UrlResponseDto generateAndPersistShortUrl(@NonNull final UrlRequestDto url);

    public Url getShortUrl(final String url);

    public Boolean isValidUrl(final String url);

    public Boolean deleteShortUrl(final String url);
}