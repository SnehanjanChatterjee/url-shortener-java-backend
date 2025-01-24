package com.url_shortener_java_backend.url_shortener_java_backend.service;

import com.url_shortener_java_backend.url_shortener_java_backend.dto.UrlRequestDto;
import com.url_shortener_java_backend.url_shortener_java_backend.dto.UrlResponseDto;
import com.url_shortener_java_backend.url_shortener_java_backend.model.Url;
import com.url_shortener_java_backend.url_shortener_java_backend.repository.UrlShortenerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static com.url_shortener_java_backend.url_shortener_java_backend.constants.UrlShortenerConstant.SHORTENED_URL_BASE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UrlShortenerServiceImplTest {

    private static final String ORIGINAL_URL = "https://www.example.com";
    private static final String SHORT_URL_CODE = "12345";
    private static final String SHORTENED_URL = SHORTENED_URL_BASE + SHORT_URL_CODE;
    private static final LocalDateTime CURRENT_TIME = LocalDateTime.of(2022, 1, 1, 12, 0, 0);
    private static final LocalDateTime EXPIRATION_TIME = CURRENT_TIME.plusDays(30);

    @Mock
    private UrlShortenerRepository urlShortenerRepository;

    @InjectMocks
    private UrlShortenerServiceImpl urlShortenerService;

    @Test
    void testGenerateAndPersistShortUrl_NullPayload() {
        assertThrows(NullPointerException.class, () -> urlShortenerService.generateAndPersistShortUrl(null));
    }

    @Test
    void testGenerateAndPersistShortUrl() {
        // Arrange
        when(urlShortenerRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        UrlRequestDto urlRequestDto = UrlRequestDto.builder().url(ORIGINAL_URL).build();

        // Act
        UrlResponseDto result = urlShortenerService.generateAndPersistShortUrl(urlRequestDto);

        // Assert
        assertNotNull(result);
        assertEquals(ORIGINAL_URL, result.getOriginalUrl());
        assertTrue(result.getShortUrl().contains(SHORTENED_URL_BASE));

        verify(urlShortenerRepository).save(any(Url.class));
    }

    @Test
    void testGetOriginalUrl_Found() throws Exception {
        // Arrange
        Url url = Url.builder()
                .shortUrl(SHORTENED_URL)
                .originalUrl(ORIGINAL_URL)
                .createdAt(CURRENT_TIME)
                .expiresAt(EXPIRATION_TIME)
                .build();
        when(urlShortenerRepository.findByShortUrl(SHORTENED_URL)).thenReturn(url);

        // Act
        UrlResponseDto result = urlShortenerService.getOriginalUrl(SHORT_URL_CODE);

        // Assert
        assertNotNull(result);
        assertEquals(ORIGINAL_URL, result.getOriginalUrl());
        assertEquals(SHORTENED_URL, result.getShortUrl());

        verify(urlShortenerRepository).findByShortUrl(SHORTENED_URL);
    }

    @Test
    void testGetOriginalUrl_NotFound() {
        // Arrange
        when(urlShortenerRepository.findByShortUrl(SHORTENED_URL)).thenReturn(null);

        // Act and Assert
        Exception exception = assertThrows(Exception.class, () -> urlShortenerService.getOriginalUrl(SHORT_URL_CODE));
        assertTrue(exception.getMessage().contains("No original url found"));

        verify(urlShortenerRepository).findByShortUrl(SHORTENED_URL);
    }

    @Test
    void testGetAllOriginalUrls() {
        // Arrange
        Url url1 = Url.builder()
                .shortUrl(SHORTENED_URL)
                .originalUrl(ORIGINAL_URL)
                .createdAt(CURRENT_TIME)
                .expiresAt(EXPIRATION_TIME)
                .build();
        Url url2 = Url.builder()
                .shortUrl(SHORTENED_URL_BASE + "67890")
                .originalUrl("https://www.example2.com")
                .createdAt(CURRENT_TIME)
                .expiresAt(EXPIRATION_TIME)
                .build();
        when(urlShortenerRepository.findAll()).thenReturn(List.of(url1, url2));

        // Act
        List<UrlResponseDto> results = urlShortenerService.getAllOriginalUrls();

        // Assert
        assertEquals(2, results.size());
        assertEquals(ORIGINAL_URL, results.get(0).getOriginalUrl());
        assertEquals(SHORTENED_URL, results.get(0).getShortUrl());

        verify(urlShortenerRepository).findAll();
    }

    @Test
    void testDeleteShortUrl() {
        // Arrange
        Url url = Url.builder()
                .shortUrl(SHORTENED_URL)
                .originalUrl(ORIGINAL_URL)
                .createdAt(CURRENT_TIME)
                .expiresAt(EXPIRATION_TIME)
                .build();
        when(urlShortenerRepository.findByShortUrl(SHORTENED_URL)).thenReturn(url);

        // Act
        urlShortenerService.deleteShortUrl(SHORT_URL_CODE);

        // Assert
        verify(urlShortenerRepository).findByShortUrl(SHORTENED_URL);
        verify(urlShortenerRepository).delete(url);
    }

    @Test
    void testDeleteShortUrl_NotFound() {
        // Arrange
        when(urlShortenerRepository.findByShortUrl(SHORTENED_URL)).thenReturn(null);

        // Act
        urlShortenerService.deleteShortUrl(SHORT_URL_CODE);

        // Assert
        verify(urlShortenerRepository).findByShortUrl(SHORTENED_URL);
        verify(urlShortenerRepository).delete(null);
    }
}
