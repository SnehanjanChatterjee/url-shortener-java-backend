package com.url_shortener_java_backend.url_shortener_java_backend.util;

import com.url_shortener_java_backend.url_shortener_java_backend.constants.UrlShortenerConstant;
import com.url_shortener_java_backend.url_shortener_java_backend.dto.UrlResponseDto;
import com.url_shortener_java_backend.url_shortener_java_backend.model.Url;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class UrlShortenerUtilTest {

    @Mock
    private UrlShortenerConstant urlShortenerConstant;

    @InjectMocks
    private UrlShortenerUtil urlShortenerUtil;

    @BeforeEach
    void setUp() {
        lenient().when(urlShortenerConstant.getShortenedUrlBase()).thenReturn("http://localhost:8080/v1.0/rest/url-shortener");
    }

    @Test
    void testGetExpirationTime() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();

        // Act
        LocalDateTime expirationTime = urlShortenerUtil.getExpirationTime(now);

        // Assert
        assertEquals(now.plusSeconds(10), expirationTime, "Expiration time should be 10 seconds after the input time.");
    }

    @Test
    void testConvertToShortUrl_ValidUrl() {
        // Arrange
        String originalUrl = "https://www.example.com";

        // Act
        String shortUrl = urlShortenerUtil.convertToShortUrl(originalUrl);

        // Assert
        assertNotNull(shortUrl, "Short URL should not be null for a valid input URL.");
        assertTrue(shortUrl.startsWith(urlShortenerConstant.getShortenedUrlBase()), "Short URL should start with the predefined base URL.");
    }

    @Test
    void testConvertToShortUrl_InvalidUrl() {
        // Arrange
        String invalidUrl = "invalid-url";

        // Act
        String shortUrl = urlShortenerUtil.convertToShortUrl(invalidUrl);

        // Assert
        assertNull(shortUrl, "Short URL should be null for an invalid input URL.");
    }

    @Test
    void testBuildUrlResponseDto() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        Url url = Url.builder()
                .originalUrl("https://www.example.com")
                .shortUrl(urlShortenerConstant.getShortenedUrlBase() + "/abc123")
                .createdAt(now)
                .expiresAt(now.plusSeconds(10))
                .build();

        // Act
        UrlResponseDto urlResponseDto = urlShortenerUtil.buildUrlResponseDto(url);

        // Assert
        assertNotNull(urlResponseDto, "UrlResponseDto should not be null.");
        assertEquals(url.getOriginalUrl(), urlResponseDto.getOriginalUrl(), "Original URL should match.");
        assertEquals(url.getShortUrl(), urlResponseDto.getShortUrl(), "Short URL should match.");
        assertEquals(url.getCreatedAt(), urlResponseDto.getCreationDateTime(), "Creation time should match.");
        assertEquals(url.getExpiresAt(), urlResponseDto.getExpirationDateTime(), "Expiration time should match.");
    }
}
