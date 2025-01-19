package com.url_shortener_java_backend.url_shortener_java_backend.util;

import com.google.common.hash.Hashing;
import com.url_shortener_java_backend.url_shortener_java_backend.dto.UrlResponseDto;
import com.url_shortener_java_backend.url_shortener_java_backend.model.Url;
import org.apache.commons.validator.routines.UrlValidator;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static com.url_shortener_java_backend.url_shortener_java_backend.constants.UrlShortenerConstant.SHORTENED_URL_BASE;

public class UrlShortenerUtil {
    public static LocalDateTime getExpirationTime(final LocalDateTime dateTime) {
        return dateTime.plusSeconds(10);
    }

    public static String convertToShortUrl(final String originalUrl) {
        final UrlValidator urlValidator = new UrlValidator(new String[]{"http", "https"});
        if (urlValidator.isValid(originalUrl)) {
            final LocalDateTime time = LocalDateTime.now();
            final String shortUrlCode = Hashing.murmur3_32_fixed()
                    .hashString(originalUrl.concat(time.toString()), StandardCharsets.UTF_8)
                    .toString();
            final String shortUrl = SHORTENED_URL_BASE.concat(shortUrlCode);
            return shortUrl;
        }
        return null;
    }

    public static UrlResponseDto buildUrlResponseDto(final Url url) {
        return UrlResponseDto.builder()
                .originalUrl(url.getOriginalUrl())
                .shortUrl(url.getShortUrl())
                .creationDateTime(url.getCreatedAt())
                .expirationDateTime(url.getExpiresAt())
                .build();
    }
}
