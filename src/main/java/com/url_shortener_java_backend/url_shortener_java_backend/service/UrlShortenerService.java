package com.url_shortener_java_backend.url_shortener_java_backend.service;

import com.url_shortener_java_backend.url_shortener_java_backend.dto.UrlRequestDto;
import com.url_shortener_java_backend.url_shortener_java_backend.dto.UrlResponseDto;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UrlShortenerService {
    public UrlResponseDto generateAndPersistShortUrl(@NonNull final UrlRequestDto url);
    public UrlResponseDto getOriginalUrl(final String shortUrlCode) throws Exception;
    public List<UrlResponseDto> getAllOriginalUrls();
    public void deleteShortUrl(final String shortUrlCode);
}
