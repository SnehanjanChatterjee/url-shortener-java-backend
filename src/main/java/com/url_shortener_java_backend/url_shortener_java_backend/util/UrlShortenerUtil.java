package com.url_shortener_java_backend.url_shortener_java_backend.util;

import com.google.common.hash.Hashing;
import com.url_shortener_java_backend.url_shortener_java_backend.constants.UrlShortenerConstant;
import com.url_shortener_java_backend.url_shortener_java_backend.dto.url.UrlResponseDto;
import com.url_shortener_java_backend.url_shortener_java_backend.dto.user.UserResponseDto;
import com.url_shortener_java_backend.url_shortener_java_backend.entity.Url;
import com.url_shortener_java_backend.url_shortener_java_backend.entity.User;
import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Component
public class UrlShortenerUtil {

    @Autowired
    private UrlShortenerConstant urlShortenerConstant;

    public LocalDateTime getExpirationTime(final LocalDateTime dateTime) {
        return dateTime.plusSeconds(10);
    }

    public String convertToShortUrl(final String originalUrl) {
        final UrlValidator urlValidator = new UrlValidator(new String[]{"http", "https"});
        if (urlValidator.isValid(originalUrl)) {
            final LocalDateTime time = LocalDateTime.now();
            final String shortUrlCode = Hashing.murmur3_32_fixed()
                    .hashString(originalUrl.concat(time.toString()), StandardCharsets.UTF_8)
                    .toString();
            final String shortUrl = urlShortenerConstant.getShortenedUrlBase().concat("/").concat(shortUrlCode);
            return shortUrl;
        }
        return null;
    }

    public UrlResponseDto buildUrlResponseDto(final Url url) {
        return UrlResponseDto.builder()
                .originalUrl(url.getOriginalUrl())
                .shortUrl(url.getShortUrl())
                .creationDateTime(url.getCreatedAt())
                .expirationDateTime(url.getExpiresAt())
                .build();
    }

    public UserResponseDto buildUserResponseDto(final User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .userId(user.getUserId())
                .name(user.getName())
                .email(user.getEmail())
                .image(user.getImage())
                .createdAt(user.getCreatedAt())
                .build();
    }

    public static String getCurrentDateTimeInIndia() {
        final ZoneId zoneId = ZoneId.of("Asia/Kolkata");
        final LocalDateTime timeInIndia = LocalDateTime.now(zoneId);
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd-MM-yyyy");
        return timeInIndia.format(formatter);
    }
}
