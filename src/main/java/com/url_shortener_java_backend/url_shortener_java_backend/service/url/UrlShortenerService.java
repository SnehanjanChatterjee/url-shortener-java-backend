package com.url_shortener_java_backend.url_shortener_java_backend.service.url;

import com.url_shortener_java_backend.url_shortener_java_backend.dto.url.UrlRequestDto;
import com.url_shortener_java_backend.url_shortener_java_backend.dto.url.UrlResponseDto;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UrlShortenerService {
    public UrlResponseDto generateAndPersistShortUrl(@NonNull final UrlRequestDto urlRequestDto);
    public UrlResponseDto getOriginalUrl(final String shortUrlCode) throws Exception;
    public List<UrlResponseDto> getAllOriginalUrls(final String userId);
    public void deleteShortUrl(final String shortUrlCode, final String userId);
    public void deleteAllShortUrls(final String userId);
}
