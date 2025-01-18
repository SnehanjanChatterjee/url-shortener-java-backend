package com.url_shortener_java_backend.url_shortener_java_backend.service;

import com.url_shortener_java_backend.url_shortener_java_backend.dto.UrlRequestDto;
import com.url_shortener_java_backend.url_shortener_java_backend.dto.UrlResponseDto;
import com.url_shortener_java_backend.url_shortener_java_backend.model.Url;
import lombok.NonNull;
import org.springframework.stereotype.Service;

@Service
public interface UrlShortenerService {
    public UrlResponseDto generateAndPersistShortUrl(@NonNull final UrlRequestDto url);

    public UrlResponseDto getOriginalUrl(final String shortUrlCode) throws Exception;

    public void deleteShortUrl(final String shortUrlCode);
}
