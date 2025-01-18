package com.url_shortener_java_backend.url_shortener_java_backend.service;

import com.url_shortener_java_backend.url_shortener_java_backend.dto.UrlRequestDto;
import com.url_shortener_java_backend.url_shortener_java_backend.dto.UrlResponseDto;
import com.url_shortener_java_backend.url_shortener_java_backend.model.Url;
import com.url_shortener_java_backend.url_shortener_java_backend.repository.UrlShortenerRepository;
import com.url_shortener_java_backend.url_shortener_java_backend.util.UrlShortenerUtil;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

import static com.url_shortener_java_backend.url_shortener_java_backend.constants.UrlShortenerConstant.SHORTENED_URL_BASE;

@Component
public class UrlShortenerServiceImpl implements UrlShortenerService {

    @Autowired
    private UrlShortenerRepository urlShortenerRepository;

    @Override
    public UrlResponseDto generateAndPersistShortUrl(@NonNull final UrlRequestDto urlRequestDto) {
        final String originalUrl = urlRequestDto.getUrl();
        final String shortenedUrl = UrlShortenerUtil.convertToShortUrl(originalUrl);
        final LocalDateTime currentTime = LocalDateTime.now();
        final LocalDateTime expirationTime = UrlShortenerUtil.getExpirationTime(currentTime);

        final Url url = Url.builder()
                .shortUrl(shortenedUrl)
                .originalUrl(originalUrl)
                .createdAt(currentTime)
                .expiresAt(expirationTime)
                .build();
        urlShortenerRepository.save(url);

        return UrlResponseDto.builder()
                .originalUrl(originalUrl)
                .shortUrl(shortenedUrl)
                .creationDateTime(currentTime)
                .expirationDateTime(expirationTime)
                .build();
    }

    @Override
    public UrlResponseDto getOriginalUrl(final String shortUrlCode) throws Exception {
        final String shortUrl = SHORTENED_URL_BASE.concat(shortUrlCode);
        final Url url = urlShortenerRepository.findByShortUrl(shortUrl);
        if (url == null) {
            throw new Exception("No original url found which have a shortened url " + shortUrl);
        }
        return UrlResponseDto.builder()
                .originalUrl(url.getOriginalUrl())
                .shortUrl(url.getShortUrl())
                .creationDateTime(url.getCreatedAt())
                .expirationDateTime(url.getExpiresAt())
                .build();
    }

    @Override
    public void deleteShortUrl(final String shortUrlCode) {
        final String shortUrl = SHORTENED_URL_BASE.concat(shortUrlCode);
        final Url url = urlShortenerRepository.findByShortUrl(shortUrl);
        urlShortenerRepository.delete(url);
    }
}
