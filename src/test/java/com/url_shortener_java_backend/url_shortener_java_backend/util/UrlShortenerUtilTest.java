package com.url_shortener_java_backend.url_shortener_java_backend.util;

import com.url_shortener_java_backend.url_shortener_java_backend.dto.UrlResponseDto;
import com.url_shortener_java_backend.url_shortener_java_backend.model.Url;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static com.url_shortener_java_backend.url_shortener_java_backend.constants.UrlShortenerConstant.SHORTENED_URL_BASE;
import static org.junit.jupiter.api.Assertions.*;

class UrlShortenerUtilTest {

    @Test
    void testGetExpirationTime() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();

        // Act
        LocalDateTime expirationTime = UrlShortenerUtil.getExpirationTime(now);

        // Assert
        assertEquals(now.plusSeconds(10), expirationTime, "Expiration time should be 10 seconds after the input time.");
    }

    @Test
    void testConvertToShortUrl_ValidUrl() {
        // Arrange
        String originalUrl = "https://www.example.com";

        // Act
        String shortUrl = UrlShortenerUtil.convertToShortUrl(originalUrl);

        // Assert
        assertNotNull(shortUrl, "Short URL should not be null for a valid input URL.");
        assertTrue(shortUrl.startsWith(SHORTENED_URL_BASE), "Short URL should start with the predefined base URL.");
    }

    @Test
    void testConvertToShortUrl_InvalidUrl() {
        // Arrange
        String invalidUrl = "invalid-url";

        // Act
        String shortUrl = UrlShortenerUtil.convertToShortUrl(invalidUrl);

        // Assert
        assertNull(shortUrl, "Short URL should be null for an invalid input URL.");
    }

    @Test
    void testBuildUrlResponseDto() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        Url url = Url.builder()
                .originalUrl("https://www.example.com")
                .shortUrl(SHORTENED_URL_BASE + "abc123")
                .createdAt(now)
                .expiresAt(now.plusSeconds(10))
                .build();

        // Act
        UrlResponseDto urlResponseDto = UrlShortenerUtil.buildUrlResponseDto(url);

        // Assert
        assertNotNull(urlResponseDto, "UrlResponseDto should not be null.");
        assertEquals(url.getOriginalUrl(), urlResponseDto.getOriginalUrl(), "Original URL should match.");
        assertEquals(url.getShortUrl(), urlResponseDto.getShortUrl(), "Short URL should match.");
        assertEquals(url.getCreatedAt(), urlResponseDto.getCreationDateTime(), "Creation time should match.");
        assertEquals(url.getExpiresAt(), urlResponseDto.getExpirationDateTime(), "Expiration time should match.");
    }
}
